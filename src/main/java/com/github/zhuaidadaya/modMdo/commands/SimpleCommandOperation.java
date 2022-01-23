package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.storage.Variables;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static com.github.zhuaidadaya.modMdo.storage.Variables.getCommandRequestVersion;

public class SimpleCommandOperation {
    public LiteralText formatModMdoVersionRequire(String commandBelong) {
        return new LiteralText("this command minimum ModMdo version require: " + getCommandRequestVersion(commandBelong));
    }

    public LiteralText formatModMdoVersionRequire(int versionRequire) {
        return new LiteralText("this command minimum ModMdo version require: " + Variables.modMdoIdToVersionMap.get(versionRequire));
    }

    public void sendFeedback(CommandContext<ServerCommandSource> source, Text message) {
        source.getSource().sendFeedback(message, false);
    }

    public void sendFeedbackAndInform(CommandContext<ServerCommandSource> source, Text message) {
        source.getSource().sendFeedback(message, true);
    }

    public void sendError(CommandContext<ServerCommandSource> source, Text message) {
        source.getSource().sendError(message);
    }

    public ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        return source.getSource().getPlayer();
    }

    public MinecraftServer getServer(CommandContext<ServerCommandSource> source) {
        return source.getSource().getServer();
    }

    public String getInput(CommandContext<ServerCommandSource> source) {
        return source.getInput();
    }
}
