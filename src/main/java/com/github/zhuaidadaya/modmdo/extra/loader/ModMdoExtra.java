package com.github.zhuaidadaya.modmdo.extra.loader;

import java.util.*;

public abstract class ModMdoExtra {
    private ExtraArgs args = new ExtraArgs();
    private String name = "ModMdoExtra(Ab.)";
    private UUID id = UUID.randomUUID();

    public abstract void init();

    public String getName() {
        return name;
    }

    public ModMdoExtra setName(String name) {
        this.name = name;
        return this;
    }

    public ModMdoExtra setArgs(ExtraArgs args) {
        this.args = args;
        return this;
    }

    public ExtraArgs getArgs() {
        return args;
    }

    public boolean hasName() {
        return !name.equals("ModMdoExtra(Ab.)");
    }

    public ModMdoExtra setId(UUID id) {
        this.id = id;
        return this;
    }

    public UUID getId() {
        return id;
    }
}
