package com.github.cao.awa.modmdo.event;

import com.github.cao.awa.modmdo.event.block.destroy.*;
import com.github.cao.awa.modmdo.event.block.place.*;
import com.github.cao.awa.modmdo.event.block.state.*;
import com.github.cao.awa.modmdo.event.client.*;
import com.github.cao.awa.modmdo.event.entity.damage.*;
import com.github.cao.awa.modmdo.event.entity.death.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.event.server.*;
import com.github.cao.awa.modmdo.event.server.chat.*;
import com.github.cao.awa.modmdo.event.server.query.*;
import com.github.cao.awa.modmdo.event.server.tick.*;
import com.github.cao.awa.modmdo.extra.loader.*;

import java.util.*;
import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoEventCenter {
    public static final HashMap<UUID, ModMdoExtra<?>> callingBuilding = new HashMap<>();

    public static void registerEntityDeath(Consumer<EntityDeathEvent> action) {
        event.entityDeath.register(action);
    }

    public static void registerEntityDamage(Consumer<EntityDamageEvent> action) {
        event.entityDamage.register(action);
    }

    public static void registerBlockDestroy(Consumer<BlockDestroyEvent> action) {
        event.blockDestroy.register(action);
    }

    public static void registerBlockStateSet(Consumer<BlockStateSetEvent> action) {
        event.blockStateSet.register(action);
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

    public static void registerGameChat(Consumer<GameChatEvent> action) {
        event.gameChat.register(action);
    }

    public static void registerClientSetting(Consumer<ClientSettingEvent> action) {
        event.clientSetting.register(action);
    }

    public static void registerServerQuery(Consumer<ServerQueryEvent> action) {
        event.serverQuery.register(action);
    }
}