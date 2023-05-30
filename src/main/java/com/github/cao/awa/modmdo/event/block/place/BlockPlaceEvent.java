package com.github.cao.awa.modmdo.event.block.place;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

@Auto
public class BlockPlaceEvent extends EntityTargetedEvent<BlockPlaceEvent> {
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

    @Override
    public String getName() {
        return "BlockPlace";
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

    @Override
    public void adaptive(BlockPlaceEvent target) {
        if (isSubmitted()) {
            action();
        } else {
            submit(target);
        }
    }

    @Override
    public String abbreviate() {
        return "BlockPlaceEvent";
    }

    public String clazz() {
        return getClass().getName();
    }

    @Override
    public ObjectArrayList<? extends Entity> getTargeted() {
        ObjectArrayList<Entity> list = new ObjectArrayList<>();
        list.add(placer);
        return list;
    }
}
