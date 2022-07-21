package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.storage.*;

import static net.minecraft.server.command.CommandManager.*;

public class BackupCommand extends SimpleCommand {
    @Override
    public BackupCommand register() {
        SharedVariables.commandRegister.register(literal("backup").then(literal("test1").executes(e -> {
//            backups.newly("");
            return 0;
        })));
        return this;
    }
}
