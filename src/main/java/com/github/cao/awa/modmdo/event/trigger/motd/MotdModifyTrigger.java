package com.github.cao.awa.modmdo.event.trigger.motd;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.server.query.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.packet.s2c.query.*;
import com.alibaba.fastjson2.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Auto
public class MotdModifyTrigger extends ModMdoEventTrigger<ServerQueryEvent> {
    private QueryResponseS2CPacket packet;
    private final ObjectArrayList<Receptacle<String>> args = new ObjectArrayList<>();
    private boolean active = true;
    private String key = "";
    private Dictionary dictionary = null;
    private String favicon = null;

    @Override
    public ModMdoEventTrigger<ServerQueryEvent> prepare(ServerQueryEvent event, JSONObject metadata, TriggerTrace trace) {
        JSONObject motd = metadata.getJSONObject("motd");
        key = motd.getString("key");
        JSONArray array = motd.getJSONArray("args");
        EntrustEnvironment.operation(args, list -> {
            for (Object o : array) {
                list.add(new Receptacle<>(o.toString()));
            }
        });
        packet = event.getPacket();
        if (motd.containsKey("dictionary")) {
            dictionary = new Dictionary(motd.getString("dictionary"));
        }
        if (motd.containsKey("favicon")) {
            favicon = motd.getString("favicon");
        }
        return this;
    }

    @Override
    public void action() {
        EntrustEnvironment.trys(() -> {
            if (favicon != null) {
                packet.getServerMetadata().setFavicon(favicon);
            }
            packet.getServerMetadata().setDescription(format().text());
        });
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

        return textFormatService.format(new Dictionary(dictionary == null ? getLanguage().getName() : dictionary.name()), key, objs);
    }
}
