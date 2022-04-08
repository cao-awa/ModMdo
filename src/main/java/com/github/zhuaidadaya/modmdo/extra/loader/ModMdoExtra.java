package com.github.zhuaidadaya.modmdo.extra.loader;

import java.util.Map;

public abstract class ModMdoExtra {
    private ExtraArgs args = new ExtraArgs();
    private String name = "ModMdoExtra(Ab.)";

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
}
