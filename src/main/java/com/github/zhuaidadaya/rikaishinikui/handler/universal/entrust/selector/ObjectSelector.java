package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.selector;

import com.github.cao.awa.modmdo.annotations.*;
import it.unimi.dsi.fastutil.objects.*;

public abstract class ObjectSelector<L, R> {
    private Object2ObjectMap<L, R> targets;

    @BecomeDeprecated
    public abstract void select();

    @BecomeDeprecated
    public void select(Object2ObjectMap<L, R> map) {
        setTargets(map);
        select();
    }

    public Object2ObjectMap<L, R> getTargets() {
        return targets;
    }

    public ObjectSelector<L, R> setTargets(Object2ObjectMap<L, R> map) {
        this.targets = map;
        return this;
    }

    public void ensure() {
        if (getTargets() == null) {
            throw new IllegalArgumentException("Select targets are not ready");
        }
    }
}
