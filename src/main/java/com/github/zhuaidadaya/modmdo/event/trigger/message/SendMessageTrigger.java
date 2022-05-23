package com.github.zhuaidadaya.modmdo.event.trigger.message;

import com.github.zhuaidadaya.modmdo.event.entity.*;
import com.github.zhuaidadaya.modmdo.event.trigger.*;
import com.github.zhuaidadaya.modmdo.event.trigger.selector.*;
import com.github.zhuaidadaya.modmdo.event.variable.*;
import com.github.zhuaidadaya.modmdo.simple.vec.*;
import com.github.zhuaidadaya.modmdo.utils.dimension.*;
import com.github.zhuaidadaya.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.json.*;

import java.util.function.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

@SingleThread
public class SendMessageTrigger<T extends EntityTargetedEvent<?>> extends ModMdoEventTrigger<T> {
    private static final Object2ObjectArrayMap<String, BiConsumer<SendMessageTrigger<?>, Receptacle<String>>> formatter = EntrustParser.operation(new Object2ObjectArrayMap<>(), map -> {
        map.put("%{dim_name}", (trigger, str) -> {
            str.set(DimensionUtil.getDimension(trigger.target.getEntityWorld()));
        });
        map.put("%{dim_color}", (trigger, str) -> {
            str.set(DimensionUtil.getDimensionColor(DimensionUtil.getDimension(trigger.target.getEntityWorld())));
        });
        map.put("%{pos}", (trigger, str) -> {
            str.set(new XYZ(trigger.target.getPos()).toString(2));
        });
        map.put("%{target_name}", (trigger, str) -> {
            String name = trigger.target.getName().asString();
            if (name.equals("")) {
                str.set(trigger.target.toString());
            } else {
                str.set(name);
            }
        });
        map.put("^{variable}", (trigger, str) -> {
            ModMdoPersistent<?> persistent = variables.get(str.getSub());
            str.set(persistent.get().toString());
        });
    });
    private String key = "";
    private final ObjectArrayList<Receptacle<String>> args = new ObjectArrayList<>();
    private EntitySelector selector = EntitySelector.SELF;
    private Entity target;
    private MinecraftServer server;

    @Override
    public void build(T event, JSONObject metadata) {
        JSONObject message = metadata.getJSONObject("message");
        key = message.getString("key");
        JSONArray array = message.getJSONArray("args");
        EntrustParser.operation(args, list -> {
            for (int i = 0;i < array.length(); i++) {
                list.add(new Receptacle<>(array.get(i).toString()));
            }
        });
        target = event.getTargeted();
        selector = EntitySelector.of(metadata.getString("selector"));
        server = target.getServer();
    }

    @Override
    public void action() {
        try {
            switch (selector) {
                case SELF -> {
                    target.sendSystemMessage(format(), target.getUuid());
                }
                case ALL -> {
                    sendMessageToAllPlayer(server, format(), false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LiteralText format() {
        for (Receptacle<String> s : args) {
            System.out.println(s.get());
            if (s.get().startsWith("{")) {
                EntrustExecution.tryTemporary(() -> {
                    JSONObject json = new JSONObject(s.get());
                    String name = json.getString("name");
                    formatter.get("^{variable}").accept(this, s.setSub(name));
                }, Throwable::printStackTrace);
            } else {
                EntrustExecution.notNull(formatter.get(s.get()), receptacle -> receptacle.accept(this, s));
            }
        }
        Object[] objs = EntrustParser.operation(new Object[args.size()], e -> {
           for (int i = 0;i < args.size();i++) {
               e[i] = args.get(i).get();
           }
        });
        User user;
        if (target instanceof ServerPlayerEntity player)
            user = loginUsers.getUser(player);
        else
            user = loginUsers.getUser(target.getUuid());

        if (user == null) {
            return minecraftTextFormat.format(getLanguage(), key, objs);
        }
        return minecraftTextFormat.format(user, key, objs);
    }
}
