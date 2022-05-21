package com.github.zhuaidadaya.modmdo.event;

import com.github.zhuaidadaya.modmdo.event.block.destroy.*;
import com.github.zhuaidadaya.modmdo.event.block.place.*;
import com.github.zhuaidadaya.modmdo.event.entity.player.death.*;

import java.util.function.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.event;

public class ModMdoEventCenter {
    public static void registerPlayerDeath(Consumer<EntityDeathEvent> action) {
        event.entityDeath.register(action);
    }

    public static void registerBlockDestroy(Consumer<BlockDestroyEvent> action) {
        event.blockDestroy.register(action);
    }

    public static void registerBlockExplosion(Consumer<BlockExplosionDestroyEvent> action) {
        event.blockExplosion.register(action);
    }

    public static void registerBlockPlace(Consumer<BlockPlaceEvent> action) {
        event.blockPlace.register(action);
    }
}
