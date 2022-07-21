package com.github.zhuaidadaya.rikaishinikui.handler.universal.selector.algorithm;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;

@Disposable
public final class ExcludeSelector<L, R> extends ObjectSelector<L, R> {
    private int exclude;

    public ExcludeSelector(int exclude) {
        this.exclude = exclude;
    }

    public ExcludeSelector(Object2ObjectMap<L, R> map, int exclude) {
        setTargets(map);
        this.exclude = exclude;
    }

    @BecomeDeprecated
    public void select() {
        ensure();
        List<L> keys = new ArrayList<>(getTargets().keySet());
        Random random = new Random();
        for (; exclude > 0 && keys.size() > 0; exclude--) {
            getTargets().remove(EntrustParser.desert(keys, random));
        }
    }

    public int getExclude() {
        return exclude;
    }

    public ExcludeSelector<L, R> setExclude(int exclude) {
        this.exclude = exclude;
        return this;
    }
}
