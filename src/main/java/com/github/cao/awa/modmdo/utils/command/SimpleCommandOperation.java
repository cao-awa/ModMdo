package com.github.cao.awa.modmdo.utils.command;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class SimpleCommandOperation {
    public static Literal formatModMdoVersionRequire(int versionRequire, ServerPlayerEntity player) {
        return minecraftTextFormat.format(loginUsers.getUser(player), "command.require.version", modMdoIdToVersionMap.get(versionRequire));
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> source, Translatable message) {
        try {
            SharedVariables.sendMessage(getPlayer(source), minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs()).text(), false);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendFeedback(ServerCommandSource source, Translatable message) {
        try {
            SharedVariables.sendMessage(getPlayer(source), minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs()).text(), false);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendFeedback(ServerPlayerEntity player, Translatable message) {
        try {
            SharedVariables.sendMessage(player, minecraftTextFormat.format(loginUsers.getUser(player), message.getKey(), message.getArgs()).text(), false);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendMessage(CommandContext<ServerCommandSource> source, Translatable message, boolean actionbar) {
        try {
            SharedVariables.sendMessage(getPlayer(source), minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs()).text(), actionbar);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendMessage(ServerCommandSource source, Translatable message, boolean actionbar) {
        try {
            SharedVariables.sendMessage(getPlayer(source), minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs()).text(), actionbar);
        } catch (Exception e) {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendMessage(ServerPlayerEntity source, Translatable message, boolean actionbar) {
        try {
            SharedVariables.sendMessage(source, minecraftTextFormat.format(loginUsers.getUser(source), message.getKey(), message.getArgs()).text(), actionbar);
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

    public static void sendFeedbackAndInform(ServerCommandSource source, Literal message) {
        try {
            source.getPlayer();
            source.sendFeedback(message.text(), true);
        } catch (Exception e) {
            LOGGER.info(message);
        }
    }

    public static void sendFeedbackAndInform(CommandContext<ServerCommandSource> source, Translatable message) {
        try {
            SharedVariables.sendMessage(getPlayer(source), TextUtil.literal(consoleTextFormat.format(message.getKey(), message.getArgs())).text(), false);
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

    public static void sendError(CommandContext<ServerCommandSource> source, Translatable message) {
        EntrustExecution.tryTemporary(() -> {
            sendError(source.getSource(), minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs()));
        }, ex -> {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        });
    }

    public static void sendError(ServerCommandSource source, Translatable message) {
        EntrustExecution.tryTemporary(() -> {
            sendError(source, minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), message.getKey(), message.getArgs()));
        }, ex -> {
            LOGGER.error(consoleTextFormat.format(message.getKey(), message.getArgs()));
        });
    }

    public static void sendError(ServerCommandSource source, Literal message) {
        try {
            source.getPlayer();
            source.sendError(message.text());
        } catch (Exception e) {
            LOGGER.info(message.getString());
        }
    }
}

