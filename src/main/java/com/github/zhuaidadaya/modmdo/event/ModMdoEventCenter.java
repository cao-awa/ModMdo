package com.github.zhuaidadaya.modmdo.event;

import com.github.zhuaidadaya.modmdo.event.block.destroy.*;
import com.github.zhuaidadaya.modmdo.event.block.place.*;
import com.github.zhuaidadaya.modmdo.event.entity.damage.*;
import com.github.zhuaidadaya.modmdo.event.entity.death.*;
import com.github.zhuaidadaya.modmdo.event.entity.player.*;
import com.github.zhuaidadaya.modmdo.event.server.*;
import com.github.zhuaidadaya.modmdo.event.server.tick.*;

import java.util.function.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.event;

public class ModMdoEventCenter {
    public static void registerEntityDeath(Consumer<EntityDeathEvent> action) {
        event.entityDeath.register(action);
    }

    public static void registerEntityDamage(Consumer<EntityDamageEvent> action) {
        event.entityDamage.register(action);
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

    public static void registerJoinServer(Consumer<JoinServerEvent> action) {
        event.joinServer.register(action);
    }

    public static void registerGameTickStart(Consumer<GameTickStartEvent> action) {
        event.gameTickStart.register(action);
    }

    public static void registerServerStarted(Consumer<ServerStartedEvent> action) {
        event.serverStarted.register(action);
    }
}
