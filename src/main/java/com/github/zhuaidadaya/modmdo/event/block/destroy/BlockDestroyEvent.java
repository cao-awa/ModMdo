package com.github.zhuaidadaya.modmdo.event.block.destroy;

import com.github.zhuaidadaya.modmdo.event.*;
import com.github.zhuaidadaya.modmdo.event.delay.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;

public class BlockDestroyEvent extends ModMdoEvent<BlockDestroyEvent> {
    private final PlayerEntity destroyBy;
    private final BlockState state;
    private final BlockPos pos;
    private final World world;
    private final MinecraftServer server;

    public BlockDestroyEvent(PlayerEntity destroyBy, BlockState state, BlockPos pos, World world, MinecraftServer server) {
        this.destroyBy = destroyBy;
        this.state = state;
        this.pos = pos;
        this.world = world;
        this.server = server;
    }

    private BlockDestroyEvent() {
        this.destroyBy = null;
        this.state = null;
        this.pos = null;
        this.world = null;
        this.server = null;
    }

    public static BlockDestroyEvent snap() {
        return new BlockDestroyEvent();
    }

    public World getWorld() {
        return world;
    }

    public BlockState getState() {
        return state;
    }

    public PlayerEntity getDestroyBy() {
        return destroyBy;
    }

    public BlockPos getPos() {
        return pos;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public BlockDestroyEvent fuse(Previously<BlockDestroyEvent> previously, BlockDestroyEvent delay) {
        return previously.target();
    }

    public String synopsis() {
        String name = EntrustParser.tryCreate(() -> {
            String str = destroyBy.getDisplayName().asString();
            if (str.equals("")) {
                throw new IllegalArgumentException("empty name");
            }
            return str;
        }, destroyBy.toString());
        return EntrustParser.tryCreate(() -> String.format("BlockDestroyEvent{block=%s, pos=%s, dimension=%s, perpetrator=%s]", Registry.BLOCK.getId(state.getBlock()), pos, world.getDimension().getEffects(), name), toString());
    }

    @Override
    public String abbreviate() {
        return "BlockDestroyEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}