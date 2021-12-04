package com.github.zhuaidadaya.modMdo.Commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BackupCommand {
    public void backup() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("backup").executes(defaultBackup -> {

                return 0;
            }).then(argument("asName", StringArgumentType.string()).executes(asNameBackup -> {

                return 1;
            })));
        });
    }
}
