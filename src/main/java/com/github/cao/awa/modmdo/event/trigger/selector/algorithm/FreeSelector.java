package com.github.cao.awa.modmdo.event.trigger.selector.algorithm;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

import java.security.*;
import java.util.*;

@Disposable
public final class FreeSelector<L, R> extends ObjectSelector<L, R> {
    public FreeSelector() {

    }

    public FreeSelector(Object2ObjectMap<L, R> map) {
        setTargets(map);
    }

    @BecomeDeprecated
    public void select() {
        ensure();
        SecureRandom random = new SecureRandom();
        L name = EntrustParser.select(new ArrayList<>(getTargets().keySet()), random);
        R json = getTargets().get(name);
        getTargets().clear();
        getTargets().put(name, json);
    }
}
