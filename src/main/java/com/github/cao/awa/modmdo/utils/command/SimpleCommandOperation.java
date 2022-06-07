package com.github.cao.awa.modmdo.utils.command;

import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class SimpleCommandOperation {
    public static LiteralTextContent formatModMdoVersionRequire(int versionRequire, ServerPlayerEntity player) {
        return minecraftTextFormat.format(loginUsers.getUser(player), "command.require.version", modMdoIdToVersionMap.get(versionRequire));
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> source, TranslatableTextContent message) {
        try {
            SharedVariables.sendMessage(getPlayer(source), MutableText.of(minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs())), false);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendFeedback(ServerCommandSource source, TranslatableTextContent message) {
        try {
            SharedVariables.sendMessage(getPlayer(source), MutableText.of(minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs())), false);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendFeedback(ServerPlayerEntity player, TranslatableTextContent message) {
        try {
            SharedVariables.sendMessage(player, MutableText.of(minecraftTextFormat.format(loginUsers.getUser(player), message.getKey(), message.getArgs())), false);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendMessage(CommandContext<ServerCommandSource> source, TranslatableTextContent message, boolean actionbar) {
        try {
            SharedVariables.sendMessage(getPlayer(source), MutableText.of(minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs())), actionbar);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendMessage(ServerCommandSource source, TranslatableTextContent message, boolean actionbar) {
        try {
            SharedVariables.sendMessage(getPlayer(source), MutableText.of(minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs())), actionbar);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendMessage(ServerPlayerEntity source, TranslatableTextContent message, boolean actionbar) {
        try {
            SharedVariables.sendMessage(source, MutableText.of(minecraftTextFormat.format(loginUsers.getUser(source), message.getKey(), message.getArgs())), actionbar);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        return SimpleCommandOperation.getPlayer(source.getSource());
    }

    public static ServerPlayerEntity getPlayer(ServerCommandSource source) throws CommandSyntaxException {
        try {
            return source.getPlayer();
        } catch (Exception e) {
            return null;
        }
    }

    public static void sendFeedbackAndInform(ServerCommandSource source, LiteralTextContent message) {
        try {
            source.getPlayer();
            source.sendFeedback(MutableText.of(message), true);
        } catch (Exception e) {
            LOGGER.info(message);
        }
    }

    public static void sendFeedbackAndInform(CommandContext<ServerCommandSource> source, TranslatableTextContent message) {
        try {
            SharedVariables.sendMessage(getPlayer(source),MutableText.of( new LiteralTextContent(consoleTextFormat.format(message.getKey(), message.getArgs()))), false);
        } catch (Exception e) {

        }
    }

    public static MinecraftServer getServer(CommandContext<ServerCommandSource> source) {
        return getServer(source.getSource());
    }

    public static MinecraftServer getServer(ServerCommandSource source) {
        return source.getServer();
    }

    public static String getInput(CommandContext<ServerCommandSource> source) {
        return source.getInput();
    }

    public static ClientConnection getConnection(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        return getConnection(source.getSource());
    }

    public static ClientConnection getConnection(ServerCommandSource source) throws CommandSyntaxException {
        return source.getPlayer().networkHandler.connection;
    }

    public static void sendError(CommandContext<ServerCommandSource> source, TranslatableTextContent message) {
        EntrustExecution.tryTemporary(() -> {
            sendError(source.getSource(), minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs()));
        }, ex -> {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        });
    }

    public static void sendError(ServerCommandSource source, TranslatableTextContent message) {
        EntrustExecution.tryTemporary(() -> {
            sendError(source, minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs()));
        }, ex -> {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        });
    }

    public static void sendError(ServerCommandSource source, LiteralTextContent message) {
        try {
            source.getPlayer();
            source.sendError(MutableText.of(message));
        } catch (Exception e) {
            LOGGER.info(message.string());
        }
    }
}

