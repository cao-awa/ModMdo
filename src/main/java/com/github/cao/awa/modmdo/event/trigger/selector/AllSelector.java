package com.github.cao.awa.modmdo.event.trigger.selector;

import com.github.cao.awa.modmdo.annotations.*;
import com.alibaba.fastjson2.*;

import java.util.function.*;

@Auto
public class AllSelector extends TriggerSelector {
    @Override
    public void select(JSONObject json, BiConsumer<String, JSONObject> operation) {
        for (String name : json.keySet()) {
            getTargets().put(name, json.getJSONObject(name));
        }
        accept(operation);
    }
}
