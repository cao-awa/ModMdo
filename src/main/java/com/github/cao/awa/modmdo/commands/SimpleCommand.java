package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.utils.command.*;
import com.mojang.brigadier.tree.*;
import net.minecraft.server.command.*;

public abstract class SimpleCommand extends SimpleCommandOperation {
    private boolean loaded = false;

    public abstract SimpleCommand register();

    public abstract void unregister();

    public void markUnload() {
        loaded = false;
    }

    public void markLoad() {
        loaded = true;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public abstract String path();

    public abstract CommandNode<ServerCommandSource> builder();

    public abstract String level();
}
