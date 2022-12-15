package com.github.cao.awa.modmdo.event.command;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.delay.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;

public class CommandExecutionEvent extends ModMdoEvent<CommandExecutionEvent> {
    private final ServerCommandSource source;
    private final String command;
    private final MinecraftServer server;

    private CommandExecutionEvent() {
        this.source = null;
        this.command = null;
        this.server = null;
    }

    public CommandExecutionEvent(ServerCommandSource source, String command, MinecraftServer server) {
        this.source = source;
        this.command = command;
        this.server = server;
    }

    @Override
    public String getName() {
        return "CommandExecution";
    }

    public static CommandExecutionEvent snap() {
        return new CommandExecutionEvent();
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
    public CommandExecutionEvent fuse(Previously<CommandExecutionEvent> previously, CommandExecutionEvent delay) {
        return previously.target();
    }

    @Override
    public String abbreviate() {
        return "CommandExecutionEvent";
    }

    @Override
    public String clazz() {
        return getClass().getName();
    }
}
