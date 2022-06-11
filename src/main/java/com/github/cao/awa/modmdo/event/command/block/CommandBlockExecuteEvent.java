package com.github.cao.awa.modmdo.event.command.block;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.block.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

@Auto
public class CommandBlockExecuteEvent extends ModMdoEvent<CommandBlockExecuteEvent> {
    private final CommandBlockExecutor executor;
    private final BlockPos pos;
    private final BlockState state;
    private final World world;
    private final MinecraftServer server;

    private CommandBlockExecuteEvent() {
        this.executor = null;
        this.pos = null;
        this.state = null;
        this.server = null;
        this.world = null;
    }

    public CommandBlockExecuteEvent(BlockPos pos, BlockState state, CommandBlockExecutor executor, World world, MinecraftServer server) {
        this.pos = pos;
        this.state = state;
        this.executor = executor;
        this.server = server;
        this.world = world;
    }

    public static CommandBlockExecuteEvent snap() {
        return new CommandBlockExecuteEvent();
    }

    public CommandBlockExecutor getExecutor() {
        return executor;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockState getState() {
        return state;
    }

    public World getWorld() {
        return world;
    }

    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public CommandBlockExecuteEvent fuse(Previously<CommandBlockExecuteEvent> previously, CommandBlockExecuteEvent delay) {
        return previously.target();
    }

    @Override
    public String synopsis() {
        return EntrustParser.tryCreate(() -> String.format("CommandBlockExecuteEvent{command=%s, pos=%s}", executor.getCommand(), pos.toString()), toString());
    }

    @Override
    public String abbreviate() {
        return "CommandBlockExecuteEvent";
    }

    @Override
    public String clazz() {
        return getClass().getName();
    }
}
