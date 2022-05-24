package com.github.cao.awa.modmdo.event.trigger.persistent;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@SingleThread
public class PersistentModifyTrigger<T extends ModMdoEvent<?>> extends ModMdoEventTrigger<T> {
    private final ObjectArrayList<Receptacle<JSONObject>> vars = new ObjectArrayList<>();

    @Override
    public ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace trace) {
        JSONArray variables = metadata.getJSONArray("variables");
        EntrustParser.operation(vars, list -> {
            for (OperationalInteger i = new OperationalInteger(); i.get() < variables.length(); i.add()) {
                EntrustExecution.tryTemporary(() -> {
                    list.add(new Receptacle<>(new JSONObject(variables.get(i.get()).toString())));
                }, ex -> {
                    err("Cannot format variable: <V." + i.get() + ">", ex);
                });
            }
        });
        setTrace(trace);
        return this;
    }

    @Override
    public void action() {
        EntrustExecution.tryTemporary(() -> {
            for (Receptacle<JSONObject> json : vars) {
                String name = EntrustParser.trying(() -> {
                    return json.get().getString("name");
                }, ex -> {
                    err("Cannot format variable", ex);
                    return null;
                });
                if (name == null) {
                    break;
                }
                EntrustExecution.tryTemporary(() -> {
                    variables.get(name).handle(json.get());
                }, e -> {
                    err("Cannot find target variable: " + name, e);
                });
            }
        });
    }

    public void err(String message, Exception exception) {
        LOGGER.warn(message + buildAt(), exception);
    }
}
