package com.github.cao.awa.modmdo.event.trigger.message;

import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.selector.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@SingleThread
public class SendMessageTrigger<T extends EntityTargetedEvent<?>> extends TargetedTrigger<T> {
    private final ObjectArrayList<Receptacle<String>> args = new ObjectArrayList<>();
    private boolean active = true;
    private String key = "";
    private EntitySelector selector = EntitySelector.SELF;
    private MinecraftServer server;

    @Override
    public ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace trace) {
        JSONObject message = metadata.getJSONObject("message");
        key = message.getString("key");
        JSONArray array = message.getJSONArray("args");
        EntrustParser.operation(args, list -> {
            for (int i = 0; i < array.length(); i++) {
                list.add(new Receptacle<>(array.get(i).toString()));
            }
        });
        setTarget(event.getTargeted());
        selector = EntitySelector.of(metadata.getString("selector"));
        server = getTarget().get(0).getServer();
        setTrace(trace);
        return this;
    }

    @Override
    public void action() {
        EntrustExecution.tryTemporary(() -> {
            send(format());
        });
    }

    public void send(Text message) {
        if (active) {
            switch (selector) {
                case SELF -> {
                    if (getTarget().size() > 1) {
                        err("Cannot use \"SELF\" selector in targeted more than one", new IllegalArgumentException("Unable to process \"SELF\" selector for entities, it need appoint an entity"));
                        return;
                    }
                    getTarget().forEach(target -> target.sendSystemMessage(message, target.getUuid()));
                }
                case ALL -> sendMessageToAllPlayer(server, message, false);
            }
        }
    }

    public void err(String message, Exception exception) {
        LOGGER.warn(message + buildAt(), exception);
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
}
