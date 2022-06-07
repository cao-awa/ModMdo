package com.github.cao.awa.modmdo.extra.loader;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.extra.loader.parameter.*;

import java.util.*;

public abstract class ModMdoExtra<T> {
    private UncertainParameter args = new UncertainParameter();
    private String name = "ModMdoExtra(Ab.)";
    private UUID id = UUID.randomUUID();

    public void auto() {
        auto(true);
    }

    public void auto(boolean active) {
        initStaticCommand();
        if (active) {
            init();
            initCommand();
            ModMdoEventCenter.callingBuilding.put(id, this);
        }
    }

    public abstract void init();

    public abstract void initCommand();

    public abstract void initStaticCommand();

    public abstract void initEvent();

    public abstract boolean needEnsure();

    public String getName() {
        return name;
    }

    public ModMdoExtra<T> setName(String name) {
        this.name = name;
        return this;
    }

    public UncertainParameter getArgs() {
        return args;
    }

    public ModMdoExtra<T> setArgs(UncertainParameter args) {
        this.args = args;
        return this;
    }

    public boolean hasName() {
        return ! name.equals("ModMdoExtra(Ab.)");
    }

    public UUID getId() {
        return id;
    }

    public ModMdoExtra<T> setId(UUID id) {
        this.id = id;
        return this;
    }
}
