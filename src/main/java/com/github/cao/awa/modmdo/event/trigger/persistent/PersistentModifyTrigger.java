package com.github.cao.awa.modmdo.event.trigger.persistent;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import com.alibaba.fastjson2.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Auto
public class PersistentModifyTrigger<T extends ModMdoEvent<?>> extends ModMdoEventTrigger<T> {
    private final ObjectArrayList<Receptacle<JSONObject>> vars = new ObjectArrayList<>();

    @Override
    public ModMdoEventTrigger<T> prepare(T event, JSONObject metadata, TriggerTrace trace) {
        JSONArray variables = metadata.getJSONArray("variables");
        EntrustEnvironment.operation(vars, list -> {
            for (OperationalInteger i = new OperationalInteger(); i.get() < variables.size(); i.add()) {
                EntrustEnvironment.trys(() -> {
                    list.add(new Receptacle<>(JSONObject.parseObject(variables.get(i.get()).toString())));
                }, ex -> {
                    err("Cannot format variable: <V." + i.get() + ">", ex);
                });
            }
        });
        return this;
    }

    @Override
    public void action() {
        EntrustEnvironment.trys(() -> {
            for (Receptacle<JSONObject> json : vars) {
                String name = EntrustEnvironment.trys(() -> {
                    return json.get().getString("name");
                }, ex -> {
                    err("Cannot format variable", ex);
                    return null;
                });
                if (name == null) {
                    break;
                }
                EntrustEnvironment.trys(() -> {
                    VARIABLES.get(name).handle(json.get());
                }, e -> {
                    err("Cannot find target variable: " + name, e);
                });
            }
        });
    }
}
