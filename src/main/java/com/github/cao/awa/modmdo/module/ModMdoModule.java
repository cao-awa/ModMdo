package com.github.cao.awa.modmdo.module;

import com.github.cao.awa.modmdo.extra.loader.*;

import java.util.*;

public abstract class ModMdoModule<T> {
    private final ModMdoExtra<T> parent;

    public ModMdoExtra<T> getParent() {
        return parent;
    }

    public abstract String getName();

    public abstract void load();

    public abstract List<String> unload(String path);

    public ModMdoModule(ModMdoExtra<T> parent) {
        this.parent = parent;
    }
}
