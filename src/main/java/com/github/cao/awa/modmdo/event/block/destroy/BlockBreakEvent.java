package com.github.cao.awa.modmdo.event.block.destroy;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

@Auto
public class BlockBreakEvent extends EntityTargetedEvent<BlockBreakEvent> {
    private final PlayerEntity destroyBy;
    private final BlockState state;
    private final BlockPos pos;
    private final World world;
    private final MinecraftServer server;

    public BlockBreakEvent(PlayerEntity destroyBy, BlockState state, BlockPos pos, World world, MinecraftServer server) {
        this.destroyBy = destroyBy;
        this.state = state;
        this.pos = pos;
        this.world = world;
        this.server = server;
    }

    private BlockBreakEvent() {
        this.destroyBy = null;
        this.state = null;
        this.pos = null;
        this.world = null;
        this.server = null;
    }

    @Override
    public String getName() {
        return "BlockBreak";
    }

    public static BlockBreakEvent snap() {
        return new BlockBreakEvent();
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

    public BlockBreakEvent fuse(Previously<BlockBreakEvent> previously, BlockBreakEvent delay) {
        return previously.target();
    }

    @Override
    public String abbreviate() {
        return "BlockDestroyEvent";
    }

    public String clazz() {
        return getClass().getName();
    }

    @Override
    public ObjectArrayList<? extends Entity> getTargeted() {
        ObjectArrayList<Entity> list = new ObjectArrayList<>();
        list.add(destroyBy);
        return list;
    }

    public void adaptive(BlockBreakEvent event) {
        if (isSubmitted()) {
            action();
        } else {
            submit(event);
        }
    }
}