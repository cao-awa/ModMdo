package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.commands.argument.*;
import com.github.zhuaidadaya.modmdo.utils.command.*;
import com.github.zhuaidadaya.modmdo.utils.times.*;
import com.github.zhuaidadaya.modmdo.whitelist.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import net.fabricmc.fabric.api.command.v1.*;
import net.minecraft.command.argument.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TemporaryWhitelistCommand extends SimpleCommandOperation implements SimpleCommand {
    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("temporary").then(literal("whitelist").then(literal("add").then(argument("name", StringArgumentType.string()).executes(addDefault -> {
                String name = StringArgumentType.getString(addDefault, "name");
                if (temporaryWhitelist.containsKey(name)) {
                    sendFeedback(addDefault, new TranslatableText("temporary.whitelist.add.already.is.whitelist", name), 21);
                    return - 1;
                }
                if (whitelist.containsKey(name)) {
                    sendFeedback(addDefault, new TranslatableText("modmdo.whitelist.add.already.is.whitelist", name), 21);
                    return - 1;
                }
                temporary(name, 1000 * 60 * 5);
                sendFeedback(addDefault, new TranslatableText("temporary.whitelist.add.default", name), 21);
                updateTemporaryWhitelistNames(getServer(addDefault));
                return 0;
            }))).then(literal("list").executes(showTemporary -> {
                showTemporary(showTemporary);
                updateTemporaryWhitelistNames(getServer(showTemporary));
                return 0;
            })).then(literal("remove").then(argument("name",ModMdoTemporaryWhitelistArgumentType.whitelist()).executes(remove -> {
                TemporaryWhitelist wl = ModMdoTemporaryWhitelistArgumentType.getWhiteList(remove,"name");
                temporaryWhitelist.remove(wl.name());
                sendFeedback(remove, new TranslatableText("temporary.whitelist.removed", wl.name()));
                updateTemporaryWhitelistNames(getServer(remove));
                return 0;
            })))));
        });
    }

    public void showTemporary(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        flushTemporaryWhitelist();
        ServerPlayerEntity player = getPlayer(source);
        if (temporaryWhitelist.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (TemporaryWhitelist wl : temporaryWhitelist.values()) {
                long million = wl.millions() - TimeUtil.processMillion(wl.recording());
                long minute = TimeUtil.processRemainingMinutes(million);
                long second = TimeUtil.processRemainingSeconds(million);
                builder.append(wl.name()).append(": ");
                builder.append(consoleTextFormat.format("temporary.whitelist.left.times", minute, second));
                builder.append("\n");
            }
            builder.delete(builder.length() - 1, builder.length());
            sendMessage(player, new TranslatableText("commands.temporary.whitelist.list", temporaryWhitelist.size(), builder.toString()), false, 22);
        } else {
            sendMessage(player, new TranslatableText("commands.temporary.whitelist.none"), false, 22);

        }
    }

    public void temporary(String name, long millions) {
        temporaryWhitelist.put(name, new TemporaryWhitelist(name, TimeUtil.millions(), millions));
    }
}
