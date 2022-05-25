package com.github.cao.awa.modmdo.event.block.place;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;

@Auto
public class BlockPlaceEvent extends ModMdoEvent<BlockPlaceEvent> {
    private final LivingEntity placer;
    private final BlockState state;
    private final BlockPos pos;
    private final World world;
    private final ItemStack itemStack;
    private final MinecraftServer server;

    public BlockPlaceEvent(LivingEntity placer, BlockState state, BlockPos pos, World world, ItemStack itemStack, MinecraftServer server) {
        this.placer = placer;
        this.state = state;
        this.pos = pos;
        this.world = world;
        this.itemStack = itemStack;
        this.server = server;
    }

    private BlockPlaceEvent() {
        this.placer = null;
        this.state = null;
        this.pos = null;
        this.world = null;
        this.itemStack = null;
        this.server = null;
    }

    public static BlockPlaceEvent snap() {
        return new BlockPlaceEvent();
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public World getWorld() {
        return world;
    }

    public BlockState getState() {
        return state;
    }

    public LivingEntity getPlacer() {
        return placer;
    }

    public BlockPos getPos() {
        return pos;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public BlockPlaceEvent fuse(Previously<BlockPlaceEvent> previously, BlockPlaceEvent delay) {
        return previously.target();
    }

    public String synopsis() {
        String name = EntrustParser.tryCreate(() -> {
            String str = placer.getDisplayName().asString();
            if (str.equals("")) {
                throw new IllegalArgumentException("empty name");
            }
            return str;
        }, placer.toString());
        return EntrustParser.tryCreate(() -> String.format("BlockPlaceEvent{block=%s, pos=%s, dimension=%s, placer=%s}", Registry.BLOCK.getId(state.getBlock()), pos, world.getDimension().getEffects(), name), toString());
    }

    @Override
    public String abbreviate() {
        return "BlockPlaceEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}
