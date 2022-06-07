package com.github.cao.awa.modmdo.extra.loader.parameter;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;

import java.util.Map;

public class UncertainParameter extends Parameter<UncertainParameter, Object>{
    private final Map<String, Object> args = new Object2ObjectRBTreeMap<>();

    public Object get(String name) {
        return args.get(name);
    }

    public UncertainParameter set(String name, Object value) {
        args.put(name, value);
        return this;
    }

    public UncertainParameter reset(String name) {
        args.remove(name);
        return this;
    }
}
