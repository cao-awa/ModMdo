package com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.map;

import java.util.*;
import java.util.function.*;

public class ConvertibleMapReceptacle<X, Y> {
    private final Map<X, Y> map;
    private final Map<Y, X> convert;

    public ConvertibleMapReceptacle(Map<X, Y> map, Map<Y, X> convert) {
        this.map = map;
        this.convert = convert;
        convert.clear();
        foreach((k, v) -> convert.put(v, k));
    }

    public void foreach(BiConsumer<X, Y> action) {
        map.forEach(action);
    }

    public void foreachConvert(BiConsumer<X, Y> action) {
        map.forEach(action);
    }

    public Y get(X target) {
        return map.get(target);
    }

    public X convert(Y target) {
        return convert.get(target);
    }

    public boolean containsKey(X target) {
        return map.containsKey(target);
    }

    public boolean containsValue(Y target) {
        return map.containsValue(target);
    }

    public boolean containsAllKey(Collection<X> target) {
        return map.keySet().containsAll(target);
    }

    public boolean containsAllValue(Collection<Y> target) {
        return map.values().containsAll(target);
    }

    public int size() {
        return map.size();
    }
}
