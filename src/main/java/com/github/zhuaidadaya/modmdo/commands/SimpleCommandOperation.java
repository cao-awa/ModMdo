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
    public LiteralText formatModMdoVersionRequire(int versionRequire, ServerPlayerEntity player) {
        sendToSub(player);
        return new LiteralText("this command minimum ModMdo version require: " + Variables.modMdoIdToVersionMap.get(versionRequire));
    }

    public void sendToSub(ServerPlayerEntity player) {
        sendFollowingMessage(server.getPlayerManager(), new TranslatableText("player.run.command.rejected.bad.modmdo.version", player.getName().asString()), "run_command_follow");
    }

    public void sendFeedback(CommandContext<ServerCommandSource> source, Text message) {
        sendFeedback(source.getSource(), message);
    }

    public void sendFeedbackAndInform(CommandContext<ServerCommandSource> source, Text message) {
        sendFeedbackAndInform(source.getSource(), message);
    }

    public void sendError(CommandContext<ServerCommandSource> source, Text message) {
        sendError(source.getSource(), message);
    }

    public ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        return getPlayer(source.getSource());
    }

    public MinecraftServer getServer(CommandContext<ServerCommandSource> source) {
        return getServer(source.getSource());
    }

    public String getInput(CommandContext<ServerCommandSource> source) {
        return source.getInput();
    }

    public ClientConnection getConnection(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        return getConnection(source.getSource());
    }

    public void sendFeedback(ServerCommandSource source, Text message) {
        try {
            source.getPlayer();
            source.sendFeedback(message, false);
        } catch (Exception e) {
            TranslatableText text = (TranslatableText) message;
            LOGGER.info(consoleTextFormat.format(text.getKey(), text.getArgs()));
        }
    }

    public void sendFeedbackAndInform(ServerCommandSource source, Text message) {
        try {
            source.getPlayer();
            source.sendFeedback(message, true);
        } catch (Exception e) {
            TranslatableText text = (TranslatableText) message;
            LOGGER.info(consoleTextFormat.format(text.getKey(), text.getArgs()));
        }
    }

    public void sendError(ServerCommandSource source, Text message) {
        try {
            source.getPlayer();
            source.sendError(message);
        } catch (Exception e) {
            TranslatableText text = (TranslatableText) message;
            LOGGER.info(consoleTextFormat.format(text.getKey(), text.getArgs()));
        }
    }

    public ServerPlayerEntity getPlayer(ServerCommandSource source) throws CommandSyntaxException {
        try {
            return source.getPlayer();
        } catch (Exception e) {
            return null;
        }
    }

    public MinecraftServer getServer(ServerCommandSource source) {
        return source.getServer();
    }

    public ClientConnection getConnection(ServerCommandSource source) throws CommandSyntaxException {
        return source.getPlayer().networkHandler.connection;
    }
}
