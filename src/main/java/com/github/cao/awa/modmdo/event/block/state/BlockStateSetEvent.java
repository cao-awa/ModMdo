package com.github.cao.awa.modmdo.event.block.state;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.delay.*;
import net.minecraft.block.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

@Auto
public class BlockStateSetEvent extends ModMdoEvent<BlockStateSetEvent> {
    private final BlockState state;
    private final BlockPos pos;
    private final int flags;
    private final int maxUpdateDepth;
    private final World world;
    private final MinecraftServer server;

    public BlockStateSetEvent(BlockState state, BlockPos pos, int flags, int maxUpdateDepth, World world, MinecraftServer server) {
        this.state = state;
        this.pos = pos;
        this.world = world;
        this.server = server;
        this.flags = flags;
        this.maxUpdateDepth = maxUpdateDepth;
    }

    private BlockStateSetEvent() {
        this.state = null;
        this.pos = null;
        this.world = null;
        this.server = null;
        this.flags = - 1;
        this.maxUpdateDepth = - 1;
    }

    public static BlockStateSetEvent snap() {
        return new BlockStateSetEvent();
    }

    public World getWorld() {
        return world;
    }

    public BlockState getState() {
        return state;
    }

    public int getFlags() {
        return flags;
    }

    public int getMaxUpdateDepth() {
        return maxUpdateDepth;
    }

    public BlockPos getPos() {
        return pos;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public BlockStateSetEvent fuse(Previously<BlockStateSetEvent> previously, BlockStateSetEvent delay) {
        return previously.target();
    }

    @Override
    public String abbreviate() {
        return "BlockStateSetEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}