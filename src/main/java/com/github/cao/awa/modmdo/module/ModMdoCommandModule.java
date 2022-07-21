package com.github.cao.awa.modmdo.module;

import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.module.error.type.*;
import it.unimi.dsi.fastutil.objects.*;

public abstract class ModMdoCommandModule<T> extends ModMdoModule<T> {
    private final Object2ObjectOpenHashMap<String, SimpleCommand> commands = new Object2ObjectOpenHashMap<>();
    private String mainer;

    public ModMdoCommandModule(ModMdoExtra<T> parent) {
        super(parent);
    }

    public void load(SimpleCommand command) {
        command.register();
        commands.put(command.path(), command);
    }

    public void loadMainer(SimpleCommand command) {
        load(command);
        mainer = command.path();
    }

    public String getMainer() {
        return mainer;
    }

    public abstract ModMdoModuleLoadStatusType loadCommand(String path);

    public abstract void initCommands();

    public abstract String commandLevel(String path);

    public Object2ObjectOpenHashMap<String, SimpleCommand> getCommands() {
        return commands;
    }

    public SimpleCommand getCommand(String path) {
        return commands.get(path);
    }
}
