package com.github.cao.awa.modmdo.event.trigger.message;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.selector.entity.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.list.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import com.alibaba.fastjson2.*;

import static com.github.cao.awa.modmdo.event.trigger.selector.entity.EntitySelectorType.*;
import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Auto
public class SendMessageTrigger<T extends EntityTargetedEvent<?>> extends TargetedTrigger<T> {
    private static final UnmodifiableListReceptacle<String> supported = new UnmodifiableListReceptacle<>(EntrustEnvironment.operation(new ObjectArrayList<>(), list -> {
        list.add(ServerPlayerEntity.class.getName());
    }));
    private final ObjectArrayList<Receptacle<String>> args = new ObjectArrayList<>();
    private boolean active = true;
    private String key = "";
    private EntitySelector selector;
    private Dictionary dictionary = null;

    @Override
    public ModMdoEventTrigger<T> prepare(T event, JSONObject metadata, TriggerTrace trace) {
        JSONObject message = metadata.getJSONObject("message");
        key = message.getString("key");
        JSONArray array = message.getJSONArray("args");
        EntrustEnvironment.operation(args, list -> {
            for (Object o : array) {
                list.add(new Receptacle<>(o.toString()));
            }
        });
        setTarget(event.getTargeted());
        selector = new EntitySelector(metadata.getJSONObject("selector"), this);
        setServer(event.getServer());
        if (message.containsKey("dictionary")) {
            dictionary = new Dictionary(message.getString("dictionary"));
        }
        return this;
    }

    @Override
    public void action() {
        EntrustEnvironment.trys(() -> send(format().text()));
    }

    public void send(Text message) {
        if (active) {
            selector.prepare(SELF, target -> {
                if (getTarget().size() > 1) {
                    err("Cannot use \"SELF\" selector in targeted more than one", new IllegalArgumentException("Unable to process \"SELF\" selector for entities, it need appoint an entity"));
                    return;
                }
                Entity targeted = getTarget().get(0);
                if (targeted instanceof ServerPlayerEntity player) {
                    player.sendMessage(message, false);
                }
            });
            selector.prepare(WORLD, target -> {
                EntrustEnvironment.notNull(getServer().getWorld(getTarget().get(0).world.getRegistryKey()), world -> world.getPlayers().forEach(player -> {
                    player.sendMessage(message, false);
                }));
            });
            selector.prepare(ALL, target -> {
                selector.filter(getServer().getPlayerManager().getPlayerList(), targeted -> {
                    sendMessage((ServerPlayerEntity) targeted, message, false);
                });
            });
            selector.prepare(APPOINT, target -> {
                EntrustEnvironment.notNull(getServer().getPlayerManager().getPlayer(target), targeted -> sendMessage(targeted, message, false));
            });
            selector.action();
        }
    }

    public Literal format() {
        for (Receptacle<String> s : args) {
            if (s.get().startsWith("{")) {
                String name = EntrustEnvironment.trys(() -> {
                    JSONObject json = JSONObject.parseObject(s.get());
                    return json.getString("name");
                }, ex -> {
                    err("Cannot format variable", ex);
                    return null;
                });
                if (name == null) {
                    active = false;
                    break;
                }
                EntrustEnvironment.trys(() -> {
                    BASE_FORMATTER.get("^{variable}").accept(s.setSub(name));
                }, e -> {
                    err("Cannot find target variable: " + name, e);
                    active = false;
                });
            } else {
                EntrustEnvironment.trys(() -> BASE_FORMATTER.get(s.get()).accept(s), ex -> {
                });
            }
        }

        if (! active) {
            return null;
        }
        Object[] objs = EntrustEnvironment.operation(new Object[args.size()], e -> {
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
                return textFormatService.format(user, key, objs);
            }
            return textFormatService.format(getLanguage(), key, objs);
        }
        return textFormatService.format(dictionary, key, objs);
    }

    @Override
    public boolean supported(String target) {
        return supported.contains(target);
    }
}
