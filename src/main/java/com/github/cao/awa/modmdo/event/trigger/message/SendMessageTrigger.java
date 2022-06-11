package com.github.cao.awa.modmdo.event.trigger.message;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.selector.entity.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.list.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Auto
public class SendMessageTrigger<T extends EntityTargetedEvent<?>> extends TargetedTrigger<T> {
    private static final UnmodifiableListReceptacle<String> supported = new UnmodifiableListReceptacle<>(EntrustParser.operation(new ObjectArrayList<>(), list -> {
        list.add(ServerPlayerEntity.class.getName());
    }));
    private final ObjectArrayList<Receptacle<String>> args = new ObjectArrayList<>();
    private boolean active = true;
    private String key = "";
    private EntitySelectorType selector = EntitySelectorType.SELF;
    private Dictionary dictionary = null;

    @Override
    public ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace trace) {
        setMeta(metadata);
        JSONObject message = metadata.getJSONObject("message");
        key = message.getString("key");
        JSONArray array = message.getJSONArray("args");
        EntrustParser.operation(args, list -> {
            for (int i = 0; i < array.length(); i++) {
                list.add(new Receptacle<>(array.get(i).toString()));
            }
        });
        setTarget(event.getTargeted());
        selector = EntitySelectorType.of(metadata.getString("selector"));
        setServer(getTarget().get(0).getServer());
        if (message.has("dictionary")) {
            dictionary = new Dictionary(message.getString("dictionary"));
        }
        setTrace(trace);
        return this;
    }

    @Override
    public void action() {
        EntrustExecution.tryTemporary(() -> send(format().text()));
    }

    public void send(Text message) {
        if (active) {
            switch (selector) {
                case SELF -> {
                    if (getTarget().size() > 1) {
                        err("Cannot use \"SELF\" selector in targeted more than one", new IllegalArgumentException("Unable to process \"SELF\" selector for entities, it need appoint an entity"));
                        return;
                    }
                    Entity target = getTarget().get(0);
                    target.sendSystemMessage(message, target.getUuid());
                }
                case ALL -> sendMessageToAllPlayer(getServer(), message, false);
                case APPOINT -> sendMessage(getServer().getPlayerManager().getPlayer(getMeta().has("name") ? getMeta().getString("name") : getMeta().getString("uuid")), message, false);
            }
        }
    }

    public Literal format() {
        for (Receptacle<String> s : args) {
            if (s.get().startsWith("{")) {
                String name = EntrustParser.trying(() -> {
                    JSONObject json = new JSONObject(s.get());
                    return json.getString("name");
                }, ex -> {
                    err("Cannot format variable", ex);
                    return null;
                });
                if (name == null) {
                    active = false;
                    break;
                }
                EntrustExecution.tryTemporary(() -> {
                    BASE_FORMATTER.get("^{variable}").accept(this, s.setSub(name));
                }, e -> {
                    err("Cannot find target variable: " + name, e);
                    active = false;
                });
            } else {
                EntrustExecution.tryTemporary(() -> BASE_FORMATTER.get(s.get()).accept(this, s), ex -> {
                });
            }
        }

        if (! active) {
            return null;
        }
        Object[] objs = EntrustParser.operation(new Object[args.size()], e -> {
            for (int i = 0; i < args.size(); i++) {
                e[i] = args.get(i).get();
            }
        });
        User user;
        if (getTarget().get(0) instanceof ServerPlayerEntity player)
            user = loginUsers.getUser(player);
        else
            user = loginUsers.getUser(getTarget().get(0).getUuid());

        if (dictionary == null) {
            if (user != null && user.getLanguage() != null) {
                return minecraftTextFormat.format(user, key, objs);
            }
            return minecraftTextFormat.format(getLanguage(), key, objs);
        }
        return minecraftTextFormat.format(dictionary, key, objs);
    }

    public UnmodifiableListReceptacle<String> supported() {
        return supported;
    }
}
