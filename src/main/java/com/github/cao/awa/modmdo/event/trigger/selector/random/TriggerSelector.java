package com.github.cao.awa.modmdo.event.trigger.selector.random;

import org.json.*;

import java.util.function.*;

public abstract class TriggerSelector {
    public abstract void select(JSONObject json, BiConsumer<String, JSONObject> operation);

    public abstract void build(JSONObject json);
}
