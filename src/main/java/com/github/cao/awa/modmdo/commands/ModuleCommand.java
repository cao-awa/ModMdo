package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.module.*;

public abstract class ModuleCommand extends SimpleCommand {
    private final ModMdoModule<?> module;

    public ModMdoModule<?> getModule() {
        return module;
    }

    public ModuleCommand(ModMdoModule<?> module) {
        this.module = module;
    }
}
