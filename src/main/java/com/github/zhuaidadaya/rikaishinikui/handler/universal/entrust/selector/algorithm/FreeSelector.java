package com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.selector.algorithm;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.selector.*;
import it.unimi.dsi.fastutil.objects.*;

import java.security.*;
import java.util.*;

@Disposable
public final class FreeSelector<L, R> extends ObjectSelector<L, R> {
    private static final SecureRandom random = new SecureRandom();

    public FreeSelector() {

    }

    public FreeSelector(Object2ObjectMap<L, R> map) {
        setTargets(map);
    }

    @BecomeDeprecated
    public void select() {
        ensure();
        L left = EntrustParser.select(new ArrayList<>(getTargets().keySet()), random);
        R right = getTargets().get(left);
        getTargets().clear();
        getTargets().put(left, right);
    }
}
