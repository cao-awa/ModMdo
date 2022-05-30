package com.github.cao.awa.modmdo.event.trigger.motd;

import com.github.cao.awa.modmdo.event.server.query.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.packet.s2c.query.*;
import net.minecraft.text.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;
import static com.github.cao.awa.modmdo.storage.SharedVariables.getLanguage;

public class MotdModifyTrigger extends ModMdoEventTrigger<ServerQueryEvent> {
    private QueryResponseS2CPacket packet;
    private final ObjectArrayList<Receptacle<String>> args = new ObjectArrayList<>();
    private boolean active = true;
    private String key = "";
    private Language dictionary = null;

    @Override
    public ModMdoEventTrigger<ServerQueryEvent> build(ServerQueryEvent event, JSONObject metadata, TriggerTrace trace) {
        setMeta(metadata);
        JSONObject motd = metadata.getJSONObject("motd");
        key = motd.getString("key");
        JSONArray array = motd.getJSONArray("args");
        EntrustParser.operation(args, list -> {
            for (int i = 0; i < array.length(); i++) {
                list.add(new Receptacle<>(array.get(i).toString()));
            }
        });
        packet = event.getPacket();
        if (motd.has("dictionary")) {
            dictionary = Language.ofs(motd.getString("dictionary"));
        }
        setTrace(trace);
        return this;
    }

    @Override
    public void action() {
        EntrustExecution.tryTemporary(() -> packet.getServerMetadata().setDescription(format()));
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

        return minecraftTextFormat.format(dictionary == null ? getLanguage() : dictionary, key, objs);
    }
}
