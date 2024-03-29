package com.github.cao.awa.modmdo.extra.loader;

import com.github.cao.awa.modmdo.event.*;

import java.util.*;

public abstract class ModMdoExtra<T> {
    private boolean signAuto = false;
    private String name = "ModMdoExtra<Ab.>";
    private UUID id = UUID.randomUUID();

    protected void signAuto() {
        signAuto = true;
    }

    public boolean isSignAuto() {
        return signAuto;
    }

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

    public void prepare() {

    }

    public abstract void init();

    public abstract void initCommand();

    public abstract void initStaticCommand();

    public abstract void initEvent();

    public String getName() {
        return name;
    }

    public ModMdoExtra<T> setName(String name) {
        this.name = name;
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
