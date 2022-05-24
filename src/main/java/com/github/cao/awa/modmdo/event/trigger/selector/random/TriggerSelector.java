package com.github.cao.awa.modmdo.event.trigger.selector.random;

import it.unimi.dsi.fastutil.objects.*;
import org.json.*;

import java.util.function.*;

public abstract class TriggerSelector {
    public final Object2ObjectOpenHashMap<String, JSONObject> targets = new Object2ObjectOpenHashMap<>();

    public Object2ObjectOpenHashMap<String, JSONObject> getTargets() {
        return targets;
    }

    public abstract void select(JSONObject json, BiConsumer<String, JSONObject> operation);

    public abstract void select(BiConsumer<String, JSONObject> operation);

    public abstract void build(JSONObject json);
}
