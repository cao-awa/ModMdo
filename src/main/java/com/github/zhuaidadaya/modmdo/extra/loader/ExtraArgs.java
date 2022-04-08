package com.github.zhuaidadaya.modmdo.extra.loader;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;

import java.util.Map;

public class ExtraArgs {
    private final Map<String, Object> args = new Object2ObjectRBTreeMap<>();

    public Object get(String name) {
        return args.get(name);
    }

    public ExtraArgs set(String name, Object value) {
        args.put(name, value);
        return this;
    }
}
