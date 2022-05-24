package com.github.cao.awa.modmdo.event.trigger.selector.random;

import org.json.*;

import java.util.function.*;

public class AllSelector extends TriggerSelector {
    @Override
    public void select(JSONObject json, BiConsumer<String, JSONObject> operation) {
        for (String name : json.keySet()) {
            operation.accept(name, json.getJSONObject(name));
        }
    }

    @Override
    public void build(JSONObject json) {

    }
}
