package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.storage.*;
import com.mojang.brigadier.tree.*;
import net.minecraft.server.command.*;

import static net.minecraft.server.command.CommandManager.*;

public class BackupCommand extends SimpleCommand {
    private final CommandNode<ServerCommandSource> builder = literal("backup").then(literal("test1").executes(e -> {
        //            backups.newly("");
        return 0;
    })).build();

    @Override
    public BackupCommand register() {
        SharedVariables.commandRegister.register("modmdo/", this, null);
        return this;
    }

    @Override
    public void unregister() {

    }

    @Override
    public String path() {
        return "modmdo/backup";
    }

    @Override
    public CommandNode<ServerCommandSource> builder() {
        return builder;
    }

    @Override
    public String level() {
        return "modmdo/backup";
    }
}
