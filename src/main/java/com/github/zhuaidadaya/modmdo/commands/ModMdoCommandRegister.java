package com.github.zhuaidadaya.modmdo.commands;

import com.mojang.brigadier.builder.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;

public record ModMdoCommandRegister(MinecraftServer server) {
    public MinecraftServer getServer() {
        return server;
    }

    public void register(LiteralArgumentBuilder<ServerCommandSource> command) {
        server.getCommandManager().getDispatcher().register(command);
    }
}
