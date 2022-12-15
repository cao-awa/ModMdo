package com.github.cao.awa.modmdo.event;

import com.github.cao.awa.modmdo.event.block.destroy.*;
import com.github.cao.awa.modmdo.event.block.place.*;
import com.github.cao.awa.modmdo.event.block.state.*;
import com.github.cao.awa.modmdo.event.client.*;
import com.github.cao.awa.modmdo.event.command.*;
import com.github.cao.awa.modmdo.event.command.block.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.entity.damage.*;
import com.github.cao.awa.modmdo.event.entity.death.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.event.server.*;
import com.github.cao.awa.modmdo.event.server.chat.*;
import com.github.cao.awa.modmdo.event.server.query.*;
import com.github.cao.awa.modmdo.event.server.tick.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import it.unimi.dsi.fastutil.objects.*;
import org.apache.logging.log4j.*;

public class ModMdoEventTracer {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoEventTracer");

    public final EntityDeathEvent entityDeath = EntityDeathEvent.snap();
    public final BlockBreakEvent blockDestroy = BlockBreakEvent.snap();
    public final BlockPlaceEvent blockPlace = BlockPlaceEvent.snap();
    public final BlockStateSetEvent blockStateSet = BlockStateSetEvent.snap();
    public final BlockExplosionDestroyEvent blockExplosion = BlockExplosionDestroyEvent.snap();
    public final EntityDamageEvent entityDamage = EntityDamageEvent.snap();
    public final JoinServerEvent joinServer = JoinServerEvent.snap();
    public final QuitServerEvent quitServer = QuitServerEvent.snap();
    public final GameTickStartEvent gameTickStart = GameTickStartEvent.snap();
    public final GameTickEndEvent gameTickEnd = GameTickEndEvent.snap();
    public final ServerStartedEvent serverStarted = ServerStartedEvent.snap();
    public final GameChatEvent gameChat = GameChatEvent.snap();
    public final ClientSettingEvent clientSetting = ClientSettingEvent.snap();
    public final ServerQueryEvent serverQuery = ServerQueryEvent.snap();
    public final CommandExecutionEvent commandExecute = CommandExecutionEvent.snap();
    public final CommandBlockExecutionEvent commandBlockExecute = CommandBlockExecutionEvent.snap();
    public final Object2ObjectOpenHashMap<String, ModMdoEvent<?>> events = EntrustEnvironment.operation(
            new Object2ObjectOpenHashMap<>(),
            map -> {
                map.put(
                        entityDeath.clazz(),
                        entityDeath
                );
                map.put(
                        blockDestroy.clazz(),
                        blockDestroy
                );
                map.put(
                        blockPlace.clazz(),
                        blockPlace
                );
                map.put(
                        blockStateSet.clazz(),
                        blockStateSet
                );
                map.put(
                        blockExplosion.clazz(),
                        blockExplosion
                );
                map.put(
                        entityDamage.clazz(),
                        entityDamage
                );
                map.put(
                        joinServer.clazz(),
                        joinServer
                );
                map.put(
                        quitServer.clazz(),
                        quitServer
                );
                map.put(
                        gameTickStart.clazz(),
                        gameTickStart
                );
                map.put(
                        serverStarted.clazz(),
                        serverStarted
                );
                map.put(
                        gameChat.clazz(),
                        gameChat
                );
                map.put(
                        clientSetting.clazz(),
                        clientSetting
                );
                map.put(
                        serverQuery.clazz(),
                        serverQuery
                );
                map.put(
                        commandExecute.clazz(),
                        commandExecute
                );
                map.put(
                        commandBlockExecute.clazz(),
                        commandBlockExecute
                );
                map.put(
                        gameTickEnd.clazz(),
                        gameTickEnd
                );
            }
    );
    public final Object2ObjectOpenHashMap<String, EntityTargetedEvent<?>> targeted = EntrustEnvironment.operation(
            new Object2ObjectOpenHashMap<>(),
            map -> {
                map.put(
                        entityDeath.clazz(),
                        entityDeath
                );
                map.put(
                        blockDestroy.clazz(),
                        blockDestroy
                );
                map.put(
                        blockPlace.clazz(),
                        blockPlace
                );
                map.put(
                        blockExplosion.clazz(),
                        blockExplosion
                );
                map.put(
                        entityDamage.clazz(),
                        entityDamage
                );
                map.put(
                        joinServer.clazz(),
                        joinServer
                );
                map.put(
                        quitServer.clazz(),
                        quitServer
                );
                map.put(
                        gameTickStart.clazz(),
                        gameTickStart
                );
                map.put(
                        gameTickEnd.clazz(),
                        gameTickEnd
                );
                map.put(
                        gameChat.clazz(),
                        gameChat
                );
                map.put(
                        clientSetting.clazz(),
                        clientSetting
                );
            }
    );

    public void build() {
        ModMdoEventCenter.callingBuilding.forEach((id, extra) -> EntrustEnvironment.trys(
                extra::initEvent,
                ex -> LOGGER.debug(
                        "Extra " + id + " init failed",
                        ex
                )
        ));
    }

    public int registered() {
        OperationalInteger result = new OperationalInteger(0);
        events.forEach((name, event) -> result.add(event.registered()));
        return result.get();
    }

    public void submit(ModMdoEvent<?> event) {
        events.get(event.clazz())
              .auto(event);
    }
}
