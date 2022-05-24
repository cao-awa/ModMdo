package com.github.cao.awa.modmdo.event;

import com.github.cao.awa.modmdo.event.block.destroy.*;
import com.github.cao.awa.modmdo.event.block.place.*;
import com.github.cao.awa.modmdo.event.block.state.*;
import com.github.cao.awa.modmdo.event.entity.damage.*;
import com.github.cao.awa.modmdo.event.entity.death.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.event.server.*;
import com.github.cao.awa.modmdo.event.server.tick.*;
import com.github.cao.awa.modmdo.storage.*;

import java.util.function.*;

public class ModMdoEventCenter {
    public static void registerEntityDeath(Consumer<EntityDeathEvent> action) {
        SharedVariables.event.entityDeath.register(action);
    }

    public static void registerEntityDamage(Consumer<EntityDamageEvent> action) {
        SharedVariables.event.entityDamage.register(action);
    }

    public static void registerBlockDestroy(Consumer<BlockDestroyEvent> action) {
        SharedVariables.event.blockDestroy.register(action);
    }

    public static void registerBlockStateSet(Consumer<BlockStateSetEvent> action) {
        SharedVariables.event.blockStateSet.register(action);
    }

    public static void registerBlockExplosion(Consumer<BlockExplosionDestroyEvent> action) {
        SharedVariables.event.blockExplosion.register(action);
    }

    public static void registerBlockPlace(Consumer<BlockPlaceEvent> action) {
        SharedVariables.event.blockPlace.register(action);
    }

    public static void registerJoinServer(Consumer<JoinServerEvent> action) {
        SharedVariables.event.joinServer.register(action);
    }

    public static void registerGameTickStart(Consumer<GameTickStartEvent> action) {
        SharedVariables.event.gameTickStart.register(action);
    }

    public static void registerServerStarted(Consumer<ServerStartedEvent> action) {
        SharedVariables.event.serverStarted.register(action);
    }
}
