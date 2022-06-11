package com.github.cao.awa.modmdo.event.block.destroy;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;

@Auto
public class BlockExplosionDestroyEvent extends EntityTargetedEvent<BlockExplosionDestroyEvent> {
    private Explosion explosion;
    private final BlockState state;
    private final BlockPos pos;
    private final World world;
    private final MinecraftServer server;

    public BlockExplosionDestroyEvent(Explosion explosion, BlockState state, BlockPos pos, World world, MinecraftServer server) {
        this.explosion = explosion;
        this.state = state;
        this.pos = pos;
        this.world = world;
        this.server = server;
    }

    private BlockExplosionDestroyEvent() {
        this.explosion = null;
        this.state = null;
        this.pos = null;
        this.world = null;
        this.server = null;
    }

    public static BlockExplosionDestroyEvent snap() {
        return new BlockExplosionDestroyEvent();
    }

    public World getWorld() {
        return world;
    }

    public BlockState getState() {
        return state;
    }

    public Explosion getExplosion() {
        return explosion;
    }

    public BlockPos getPos() {
        return pos;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public BlockExplosionDestroyEvent fuse(Previously<BlockExplosionDestroyEvent> previously, BlockExplosionDestroyEvent delay) {
        previously.target().explosion = delay.explosion;
        previously.action().apply();
        return previously.target();
    }

    public String synopsis() {
        String name = EntrustParser.tryCreate(() -> {
            String str = explosion.getCausingEntity().getDisplayName().asString();
            if (str.equals("")) {
                throw new IllegalArgumentException("empty name");
            }
            return str;
        }, "");
        return EntrustParser.tryCreate(() -> String.format("BlockExplosionDestroyEvent{block=%s, pos=%s, dimension=%s, perpetrator=%s}", Registry.BLOCK.getId(state.getBlock()), pos, world.getDimension().getEffects(), name), toString());
    }

    @Override
    public String abbreviate() {
        return "BlockExplosionDestroyEvent";
    }

    public String clazz() {
        return getClass().getName();
    }

    @Override
    public ObjectArrayList<? extends Entity> getTargeted() {
        ObjectArrayList<Entity> list = new ObjectArrayList<>();
        list.add(explosion.getCausingEntity());
        return list;
    }
}
