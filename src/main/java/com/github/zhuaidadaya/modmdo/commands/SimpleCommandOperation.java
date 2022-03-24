package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.storage.Variables;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class SimpleCommandOperation {
    public LiteralText formatModMdoVersionRequire(String commandBelong, ServerPlayerEntity player) {
        sendToSub(player);
        return new LiteralText("this command minimum ModMdo version require: " + getCommandRequestVersion(commandBelong));
    }

    public LiteralText formatModMdoVersionRequire(int versionRequire, ServerPlayerEntity player) {
        sendToSub(player);
        return new LiteralText("this command minimum ModMdo version require: " + Variables.modMdoIdToVersionMap.get(versionRequire));
    }

    public void sendToSub(ServerPlayerEntity player) {
        sendFollowingMessage(server.getPlayerManager(), new TranslatableText("player.run.command.rejected.bad.modmdo.version", player.getName().asString()), "run_command_follow");
    }

    public void sendFeedback(CommandContext<ServerCommandSource> source, Text message) {
        try {
            source.getSource().getPlayer();
            source.getSource().sendFeedback(message, false);
        } catch (Exception e) {
            TranslatableText text = (TranslatableText) message;
            LOGGER.info(consoleTextFormat.format(text.getKey(), text.getArgs()));
        }
    }

    public void sendFeedbackAndInform(CommandContext<ServerCommandSource> source, Text message) {
        try {
            source.getSource().getPlayer();
            source.getSource().sendFeedback(message, true);
        } catch (Exception e) {
            TranslatableText text = (TranslatableText) message;
            LOGGER.info(consoleTextFormat.format(text.getKey(), text.getArgs()));
        }
    }

    public void sendError(CommandContext<ServerCommandSource> source, Text message) {
        try {
            source.getSource().getPlayer();
            source.getSource().sendError(message);
        } catch (Exception e) {
            TranslatableText text = (TranslatableText) message;
            LOGGER.info(consoleTextFormat.format(text.getKey(), text.getArgs()));
        }
    }

    public ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        try {
            return source.getSource().getPlayer();
        } catch (Exception e) {
            return null;
        }
    }

    public MinecraftServer getServer(CommandContext<ServerCommandSource> source) {
        return source.getSource().getServer();
    }

    public String getInput(CommandContext<ServerCommandSource> source) {
        return source.getInput();
    }

    public ClientConnection getConnection(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        return source.getSource().getPlayer().networkHandler.connection;
    }
}
