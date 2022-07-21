package com.github.cao.awa.modmdo.extra.loader;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.extra.loader.parameter.*;
import com.github.cao.awa.modmdo.module.*;
import it.unimi.dsi.fastutil.objects.*;

import java.util.*;

public abstract class ModMdoExtra<T> {
    private final Object2ObjectOpenHashMap<String, ModMdoModule<T>> modules = new Object2ObjectOpenHashMap<>();
    private boolean signAuto = false;
    private UncertainParameter args = new UncertainParameter();
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
        initStaticCommands();
        if (active) {
            init();
            loadModules();
            ModMdoEventCenter.callingBuilding.put(id, this);
        }
    }

    public void prepare() {

    }

    public abstract void loadModules();

    public void loadModule(ModMdoModule<T> module) {
        modules.put(module.getName(), module);
        module.load();
    }

    public Object2ObjectOpenHashMap<String, ModMdoModule<T>> getModules() {
        return modules;
    }

    public abstract void init();

    public abstract void initStaticCommands();

    public abstract void initEvents();

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
