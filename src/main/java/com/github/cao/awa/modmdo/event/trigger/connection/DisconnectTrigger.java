package com.github.cao.awa.modmdo.event.trigger.connection;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.selector.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.set.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Auto
public class DisconnectTrigger<T extends EntityTargetedEvent<?>> extends TargetedTrigger<T> {
    private static final UnmodifiableListReceptacle<String> supported = new UnmodifiableListReceptacle<>(EntrustParser.operation(new ObjectArrayList<>(), list -> {
        list.add(ServerPlayerEntity.class.getName());
    }));
    private final ObjectArrayList<Receptacle<String>> args = new ObjectArrayList<>();
    private boolean active = true;
    private String key = "";
    private EntitySelectorType selector = EntitySelectorType.SELF;
    private MinecraftServer server;

    @Override
    public ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace trace) {
        JSONObject message = metadata.getJSONObject("reason");
        key = message.getString("key");
        JSONArray array = message.getJSONArray("args");
        EntrustParser.operation(args, list -> {
            for (int i = 0; i < array.length(); i++) {
                list.add(new Receptacle<>(array.get(i).toString()));
            }
        });
        setTarget(event.getTargeted());
        selector = EntitySelectorType.of(metadata.getString("selector"));
        server = getTarget().get(0).getServer();
        setTrace(trace);
        return this;
    }

    @Override
    public void action() {
        EntrustExecution.tryTemporary(() -> disconnect(format()));
    }

    public void disconnect(Text reason) {
        if (active) {
            switch (selector) {
                case SELF -> {
                    if (getTarget().size() > 1) {
                        err("Cannot use \"SELF\" selector in targeted more than one", new IllegalArgumentException("Unable to process \"SELF\" selector for entities, it need appoint an entity"));
                        return;
                    }
                    Entity target = getTarget().get(0);
                    if (target instanceof ServerPlayerEntity player) {
                        player.networkHandler.disconnect(reason);
                    }
                }
                case ALL -> {
                    EntrustExecution.tryFor(server.getPlayerManager().getPlayerList(), player -> {
                        player.networkHandler.disconnect(reason);
                    });
                }
            }
        }
    }

    public LiteralText format() {
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
                    formatter.get("^{variable}").accept(this, s.setSub(name));
                }, e -> {
                    err("Cannot find target variable: " + name, e);
                    active = false;
                });
            } else {
                EntrustExecution.notNull(formatter.get(s.get()), receptacle -> receptacle.accept(this, s));
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

        if (user == null) {
            return minecraftTextFormat.format(getLanguage(), key, objs);
        }
        return minecraftTextFormat.format(user, key, objs);
    }

    public UnmodifiableListReceptacle<String> supported() {
        return supported;
    }
}
