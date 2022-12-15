package com.github.cao.awa.modmdo.event.trigger.selector;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import com.alibaba.fastjson2.*;

import java.util.function.*;

public abstract class TriggerSelector {
    private final Object2ObjectOpenHashMap<String, JSONObject> targets = new Object2ObjectOpenHashMap<>();

    public abstract void select(JSONObject json, BiConsumer<String, JSONObject> operation);

    public void accept(BiConsumer<String, JSONObject> operation) {
        EntrustEnvironment.tryFor(getTargets().keySet(), name -> operation.accept(name, getTargets().get(name)));
    }

    public Object2ObjectOpenHashMap<String, JSONObject> getTargets() {
        return targets;
    }

    public void build(JSONObject json) {

    }
}
