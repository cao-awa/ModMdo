package com.github.cao.awa.modmdo.extra.loader.parameter;

import it.unimi.dsi.fastutil.objects.*;

import java.util.*;

public class DeterminateParameter<T> extends Parameter<DeterminateParameter<T>, T> {
    private final Map<String, T> args = new Object2ObjectRBTreeMap<>();

    public T get(String name) {
        return args.get(name);
    }

    public DeterminateParameter<T> set(String name, T value) {
        args.put(name, value);
        return this;
    }

    public DeterminateParameter<T> reset(String name) {
        args.remove(name);
        return this;
    }
}
