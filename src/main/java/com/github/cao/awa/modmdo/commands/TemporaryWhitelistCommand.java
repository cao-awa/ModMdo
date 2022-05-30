package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.commands.argument.whitelist.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.cao.awa.modmdo.whitelist.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import static net.minecraft.server.command.CommandManager.*;

public class TemporaryWhitelistCommand extends SimpleCommand {
    @Override
    public TemporaryWhitelistCommand register() {
        SharedVariables.commandRegister.register(literal("temporary").then(literal("whitelist").then(literal("add").then(argument("name", StringArgumentType.string()).executes(addDefault -> {
            String name = StringArgumentType.getString(addDefault, "name");
            if (SharedVariables.temporaryWhitelist.containsName(name)) {
                sendFeedback(addDefault, new TranslatableText("temporary.whitelist.add.already.is.whitelist", name));
                return - 1;
            }
            if (SharedVariables.whitelist.containsName(name)) {
                sendFeedback(addDefault, new TranslatableText("modmdo.whitelist.add.already.is.whitelist", name));
                return - 1;
            }
            temporary(name, 1000 * 60 * 5);
            sendFeedback(addDefault, new TranslatableText("temporary.whitelist.add.default", name));
            SharedVariables.updateTemporaryWhitelistNames(getServer(addDefault), true);
            return 0;
        }))).then(literal("list").executes(showTemporary -> {
            showTemporary(showTemporary);
            SharedVariables.updateTemporaryWhitelistNames(getServer(showTemporary), true);
            return 0;
        })).then(literal("remove").then(argument("name", ModMdoTemporaryWhitelistArgumentType.whitelist()).executes(remove -> {
            TemporaryWhitelist wl = ModMdoTemporaryWhitelistArgumentType.getWhiteList(remove, "name");
            if (SharedVariables.temporaryWhitelist.containsName(wl.getName())) {
                SharedVariables.temporaryWhitelist.remove(wl.name());
                sendFeedback(remove, new TranslatableText("temporary.whitelist.removed", wl.name()));
                SharedVariables.updateTemporaryWhitelistNames(getServer(remove), true);
                return 0;
            }
            sendError(remove, new TranslatableText("arguments.temporary.whitelist.not.registered", wl.getName()));
            return - 1;
        })))).then(literal("connection").then(literal("whitelist").executes(whitelist -> {
            if (SharedVariables.modmdoConnectionAccepting.isValid()) {
                long million = SharedVariables.modmdoConnectionAccepting.millions() - TimeUtil.processMillion(SharedVariables.modmdoConnectionAccepting.recording());
                long minute = TimeUtil.processRemainingMinutes(million);
                long second = TimeUtil.processRemainingSeconds(million);
                sendFeedback(whitelist, new TranslatableText("connection.whitelist.accepting", minute, second));
            } else {
                sendFeedback(whitelist, new TranslatableText("connection.whitelist.no.accepting"));
            }
            return 0;
        }).then(literal("accept").then(literal("one").executes(acceptOne -> {
            if (SharedVariables.modmdoConnectionAccepting.isValid()) {
                long million = SharedVariables.modmdoConnectionAccepting.millions() - TimeUtil.processMillion(SharedVariables.modmdoConnectionAccepting.recording());
                long minute = TimeUtil.processRemainingMinutes(million);
                long second = TimeUtil.processRemainingSeconds(million);
                sendError(acceptOne, new TranslatableText("connection.whitelist.accepting", minute, second));
            } else {
                sendFeedback(acceptOne, new TranslatableText("connection.whitelist.accepting.one"));
                SharedVariables.modmdoConnectionAccepting = new TemporaryWhitelist("", TimeUtil.millions(), 1000 * 60 * 5);
            }
            return 0;
        }))).then(literal("cancel").executes(cancel -> {
            if (SharedVariables.modmdoConnectionAccepting.isValid()) {
                SharedVariables.modmdoConnectionAccepting = new TemporaryWhitelist("", - 1, - 1);
            } else {
                sendError(cancel, new TranslatableText("connection.whitelist.no.accepting"));
            }
            return 0;
        })))));
        return this;
    }

    public void showTemporary(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        SharedVariables.flushTemporaryWhitelist();
        ServerPlayerEntity player = getPlayer(source);
        if (SharedVariables.temporaryWhitelist.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (TemporaryWhitelist wl : SharedVariables.temporaryWhitelist.values()) {
                long million = wl.millions() - TimeUtil.processMillion(wl.recording());
                long minute = TimeUtil.processRemainingMinutes(million);
                long second = TimeUtil.processRemainingSeconds(million);
                builder.append(wl.name()).append(": ");
                builder.append(SharedVariables.consoleTextFormat.format("temporary.whitelist.left.times", minute, second));
                builder.append("\n");
            }
            builder.delete(builder.length() - 1, builder.length());
            sendMessage(player, new TranslatableText("commands.temporary.whitelist.list", SharedVariables.temporaryWhitelist.size(), builder.toString()), false);
        } else {
            sendMessage(player, new TranslatableText("commands.temporary.whitelist.none"), false);

        }
    }

    public void temporary(String name, long millions) {
        SharedVariables.temporaryWhitelist.put(name, new TemporaryWhitelist(name, TimeUtil.millions(), millions));
    }
}
