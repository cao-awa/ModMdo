package com.github.cao.awa.modmdo.extra.loader.parameter;

public abstract class Parameter<T extends Parameter<?, ?>, Y> {
    public abstract Y get(String name);

    public abstract T set(String name, Y value);

    public abstract T reset(String name);
}
