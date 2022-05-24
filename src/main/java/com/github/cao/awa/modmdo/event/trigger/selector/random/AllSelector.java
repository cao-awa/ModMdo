package com.github.cao.awa.modmdo.event.trigger.selector.random;

import org.json.*;

import java.util.function.*;

public class AllSelector extends TriggerSelector {
    @Override
    public void select(JSONObject json, BiConsumer<String, JSONObject> operation) {
        for (String name : json.keySet()) {
            targets.put(name, json.getJSONObject(name));
        }
        select(operation);
    }

    public void select(BiConsumer<String, JSONObject> operation) {
        for (String name : targets.keySet()) {
            operation.accept(name, targets.get(name));
        }
    }

    @Override
    public void build(JSONObject json) {

    }
}
