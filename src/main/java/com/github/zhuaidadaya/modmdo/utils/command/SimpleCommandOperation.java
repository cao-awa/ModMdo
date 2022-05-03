package com.github.zhuaidadaya.modmdo.utils.command;

import com.github.zhuaidadaya.modmdo.storage.Variables;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class SimpleCommandOperation {
    public static LiteralText formatModMdoVersionRequire(int versionRequire, ServerPlayerEntity player) {
        sendToSub(player);
        return new LiteralText(consoleTextFormat.format("command.require.version", modMdoIdToVersionMap.get(versionRequire)));
    }

    public static void sendToSub(ServerPlayerEntity player) {
        sendFollowingMessage(server.getPlayerManager(), new TranslatableText("player.run.command.rejected.bad.modmdo.version", player.getName().asString()), "run_command_follow");
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> source, TranslatableText message, int version) {
        try {
            if (getPlayerModMdoVersion(getPlayer(source)) >= version) {
                sendFeedback(source, message);
            } else {
                Variables.sendMessage(getPlayer(source), minecraftTextFormat.format(message.getKey(), message.getArgs()), false);
            }
        } catch (Exception e) {
            LOGGER.error("failed to feedback command result: " + consoleTextFormat.format(message.getKey(), message.getArgs()));
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

    public static void sendFeedback(CommandContext<ServerCommandSource> source, TranslatableText message) {
        SimpleCommandOperation.sendFeedback(source.getSource(), message);
    }

    public static void sendFeedback(ServerCommandSource source, TranslatableText message) {
        try {
            source.getPlayer();
            source.sendFeedback(message, false);
        } catch (Exception e) {
            TranslatableText text = message;
            LOGGER.info(consoleTextFormat.format(text.getKey(), text.getArgs()));
        }
    }

    public static void sendFeedbackAndInform(CommandContext<ServerCommandSource> source, TranslatableText message) {
        sendFeedbackAndInform(source.getSource(), message);
    }

    public static void sendFeedbackAndInform(ServerCommandSource source, TranslatableText message) {
        try {
            source.getPlayer();
            source.sendFeedback(message, true);
        } catch (Exception e) {
            TranslatableText text = message;
            LOGGER.info(consoleTextFormat.format(text.getKey(), text.getArgs()));
        }
    }

    public static void sendFeedbackAndInform(ServerCommandSource source, LiteralText message) {
        try {
            source.getPlayer();
            source.sendFeedback(message, true);
        } catch (Exception e) {
            LOGGER.info(message);
        }
    }

    public static void sendFeedbackAndInform(CommandContext<ServerCommandSource> source, TranslatableText message, int version) {
        try {
            if (getPlayerModMdoVersion(getPlayer(source)) >= version) {
                sendFeedbackAndInform(source.getSource(), message);
            } else {
                Variables.sendMessage(getPlayer(source), new LiteralText(consoleTextFormat.format(message.getKey(), message.getArgs())), false);
            }
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

    public static void sendFeedback(ServerCommandSource source, TranslatableText message, int version) {
        try {
            if (getPlayerModMdoVersion(getPlayer(source)) >= version) {
                sendFeedback(source, message);
            } else {
                Variables.sendMessage(getPlayer(source), new LiteralText(consoleTextFormat.format(message.getKey(), message.getArgs())), false);
            }
        } catch (Exception e) {
            LOGGER.error("failed to feedback command result to ops: " + consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendMessage(ServerCommandSource source, TranslatableText message, boolean actionbar, int version) {
        try {
            if (getPlayerModMdoVersion(getPlayer(source)) == version) {
                Variables.sendMessage(getPlayer(source), message, actionbar);
            } else {
                Variables.sendMessage(getPlayer(source), new LiteralText(consoleTextFormat.format(message.getKey(), message.getArgs())), actionbar);
            }
        } catch (Exception e) {
            LOGGER.error("failed to send message: " + consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendMessage(CommandContext<ServerCommandSource> source, TranslatableText message, boolean actionbar, int version) {
        try {
            if (getPlayerModMdoVersion(getPlayer(source)) >= version) {
                Variables.sendMessage(getPlayer(source), message, actionbar);
            } else {
                Variables.sendMessage(getPlayer(source), new LiteralText(consoleTextFormat.format(message.getKey(), message.getArgs())), actionbar);
            }
        } catch (Exception e) {
            LOGGER.error("failed to send message: " + consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendMessage(ServerPlayerEntity source, TranslatableText message, boolean actionbar, int version) {
        try {
            if (getPlayerModMdoVersion(source) >= version) {
                Variables.sendMessage(source, message, actionbar);
            } else {
                Variables.sendMessage(source, new LiteralText(consoleTextFormat.format(message.getKey(), message.getArgs())), actionbar);
            }
        } catch (Exception e) {
            LOGGER.error("failed to send message: " + consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendError(CommandContext<ServerCommandSource> source, TranslatableText message, int version) {
        try {
            if (getPlayerModMdoVersion(getPlayer(source)) >= version) {
                sendError(source, message);
            } else {
                Variables.sendMessage(getPlayer(source), new LiteralText(consoleTextFormat.format(message.getKey(), message.getArgs())), false);
            }
        } catch (Exception e) {
            LOGGER.info(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendError(CommandContext<ServerCommandSource> source, TranslatableText message) {
        sendError(source.getSource(), message);
    }

    public static void sendError(ServerCommandSource source, TranslatableText message) {
        try {
            source.getPlayer();
            source.sendError(message);
        } catch (Exception e) {
            LOGGER.info(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }

    public static void sendError(CommandContext<ServerCommandSource> source, LiteralText message) {
        sendError(source.getSource(), message);
    }

    public static void sendError(ServerCommandSource source, LiteralText message) {
        try {
            source.getPlayer();
            source.sendError(message);
        } catch (Exception e) {
            LOGGER.info(message.asString());
        }
    }

    public static void sendError(ServerCommandSource source, TranslatableText message, int version) {
        try {
            if (getPlayerModMdoVersion(getPlayer(source)) >= version) {
                sendError(source, message);
            } else {
                Variables.sendMessage(getPlayer(source), new LiteralText(consoleTextFormat.format(message.getKey(), message.getArgs())), false);
            }
        } catch (Exception e) {
            LOGGER.info(consoleTextFormat.format(message.getKey(), message.getArgs()));
        }
    }
}

