package com.github.cao.awa.modmdo.extra.modmdo.module.whitelist.commands;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.commands.suggester.whitelist.*;
import com.github.cao.awa.modmdo.module.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.tree.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class ModMdoWhitelistCommand extends ModuleCommand {
    private final CommandNode<ServerCommandSource> builder = literal("whitelist").then(literal("remove").then(argument("name", StringArgumentType.string()).suggests(ModMdoWhitelistSuggester::suggestions).executes(remove -> {
        Certificate wl = ModMdoWhitelistSuggester.getWhiteList(StringArgumentType.getString(remove, "name"));
        if (SharedVariables.whitelist.containsName(wl.getName())) {
            SharedVariables.whitelist.remove(wl.getName());
            SimpleCommandOperation.sendFeedback(remove, TextUtil.translatable("modmdo.whitelist.removed", wl.getName()));
            SharedVariables.saveVariables();
            return 0;
        }
        SimpleCommandOperation.sendError(remove, TextUtil.translatable("arguments.permanent.whitelist.not.registered"));
        return - 1;
    }))).then(literal("list").executes(showWhiteList -> {
                showWhitelist(showWhiteList);
                return 0;
            })
            //                        .then(literal("multiple").then(argument("name", ModMdoWhitelistArgumentType.whitelist()).executes(remove -> {
            //            Certificate wl = ModMdoWhitelistArgumentType.getWhiteList(remove, "name");
            //            if (temporaryStation.containsName(wl.getName())) {
            //                SimpleCommandOperation.sendFeedback(remove, TextUtil.translatable("modmdo.whitelist.multiple.already", wl.getName()));
            //                return -1;
            //            }
            //            if (SharedVariables.whitelist.containsName(wl.getName())) {
            //                temporaryStation.put(wl.getName(), new TemporaryCertificate(wl.getName(), new LoginRecorde(null, null,LoginRecordeType.MULTIPLE), 0,0));
            //                SimpleCommandOperation.sendFeedback(remove, TextUtil.translatable("modmdo.whitelist.multiple", wl.getName()));
            //                SharedVariables.updateWhitelistNames(SimpleCommandOperation.getServer(remove), true);
            //                SharedVariables.saveVariables();
            //                return 0;
            //            }
            //            SimpleCommandOperation.sendError(remove, TextUtil.translatable("arguments.permanent.whitelist.not.registered"));
            //            return - 1;
            //        })))
    ).build();

    public ModMdoWhitelistCommand(ModMdoModule<?> module) {
        super(module);
    }

    @Override
    public SimpleCommand register() {
        commandRegister.register("modmdo/", this, getModule());
        return this;
    }

    public void showWhitelist(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        SharedVariables.handleTemporaryWhitelist();
        ServerPlayerEntity player = SimpleCommandOperation.getPlayer(source);
        if (SharedVariables.whitelist.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (Certificate wl : SharedVariables.whitelist.values()) {
                builder.append(wl.getName()).append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            SimpleCommandOperation.sendMessage(player, TextUtil.translatable("commands.modmdo.whitelist.list", SharedVariables.whitelist.size(), builder.toString()), false);
        } else {
            SimpleCommandOperation.sendMessage(player, TextUtil.translatable("commands.modmdo.whitelist.none"), false);

        }
    }

    @Override
    public void unregister() {
        commandRegister.unregister("modmdo/", this, getModule());
    }

    @Override
    public String path() {
        return "modmdo/whitelist";
    }

    @Override
    public CommandNode<ServerCommandSource> builder() {
        return builder;
    }

    @Override
    public String level() {
        return "modmdo/whitelist";
    }
}
