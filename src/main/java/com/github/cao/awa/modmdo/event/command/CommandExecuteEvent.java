package com.github.cao.awa.modmdo.event.command;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.delay.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;

public class CommandExecuteEvent extends ModMdoEvent<CommandExecuteEvent> {
    private final ServerCommandSource source;
    private final String command;
    private final MinecraftServer server;

    private CommandExecuteEvent() {
        this.source = null;
        this.command = null;
        this.server = null;
    }

    public CommandExecuteEvent(ServerCommandSource source, String command, MinecraftServer server) {
        this.source = source;
        this.command = command;
        this.server = server;
    }

    public static CommandExecuteEvent snap() {
        return new CommandExecuteEvent();
    }

    public ServerCommandSource getSource() {
        return source;
    }

    public String getCommand() {
        return command;
    }

    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public CommandExecuteEvent fuse(Previously<CommandExecuteEvent> previously, CommandExecuteEvent delay) {
        return previously.target();
    }

    @Override
    public String abbreviate() {
        return "CommandExecuteEvent";
    }

    @Override
    public String clazz() {
        return getClass().getName();
    }
}
