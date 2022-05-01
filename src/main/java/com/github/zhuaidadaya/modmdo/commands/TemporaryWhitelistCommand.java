package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.utils.command.*;
import com.github.zhuaidadaya.modmdo.utils.times.*;
import com.github.zhuaidadaya.modmdo.whitelist.*;
import com.mojang.brigadier.arguments.*;
import net.fabricmc.fabric.api.command.v1.*;
import net.minecraft.command.argument.*;
import net.minecraft.text.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.temporaryWhitelist;
import static com.github.zhuaidadaya.modmdo.storage.Variables.whitelist;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TemporaryWhitelistCommand extends SimpleCommandOperation implements SimpleCommand {
    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("temporary").then(literal("whitelist").then(literal("add").then(argument("name",StringArgumentType.string()).executes(addDefault -> {
                String name = StringArgumentType.getString(addDefault, "name");
                temporary(name, 1000 * 60 * 5);
                sendFeedback(addDefault, new TranslatableText("temporary.whitelist.add.default", name), 21);
                return 0;
            })))));
        });
    }

    public void temporary(String name, long millions) {
        temporaryWhitelist.put(name,new TemporaryWhitelist(name, TimeUtil.millions(), millions));
    }
}
