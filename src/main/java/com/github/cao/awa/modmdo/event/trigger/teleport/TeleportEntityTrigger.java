package com.github.cao.awa.modmdo.event.trigger.teleport;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.selector.entity.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.util.math.*;
import org.json.*;

import java.util.*;

import static com.github.cao.awa.modmdo.event.trigger.selector.entity.EntitySelectorType.*;

@Auto
public class TeleportEntityTrigger<T extends EntityTargetedEvent<?>> extends TargetedTrigger<T> {
    private EntitySelector selector;
    private Vec3d xyz;

    @Override
    public ModMdoEventTrigger<T> prepare(T event, JSONObject metadata, TriggerTrace trace) {
        setTarget(event.getTargeted());
        setServer(event.getServer());
        selector = new EntitySelector(metadata.getJSONObject("selector"), this);
        JSONObject pos = metadata.getJSONObject("pos");
        xyz = new Vec3d(pos.getDouble("x"), pos.getDouble("y"),pos.getDouble("z"));
        return this;
    }

    @Override
    public void action() {
        EntrustExecution.tryTemporary(() -> {
            selector.prepare(SELF, target -> {
                if (getTarget().size() > 1) {
                    err("Cannot use \"SELF\" selector in targeted more than one", new IllegalArgumentException("Unable to process \"SELF\" selector for entities, it need appoint an entity"));
                    return;
                }
                getTarget().get(0).teleport(xyz.getX(), xyz.getY(), xyz.getZ());
            });
            selector.prepare(WORLD, target -> {
                EntrustExecution.notNull(getServer().getWorld(getTarget().get(0).world.getRegistryKey()), world -> world.iterateEntities().forEach(entity -> selector.filter(entity, e -> e.teleport(xyz.getX(), xyz.getY(), xyz.getZ()))));
            });
            selector.prepare(ALL, target -> {
                getServer().getWorlds().forEach(world -> world.iterateEntities().forEach(entity -> selector.filter(entity, e -> e.teleport(xyz.getX(), xyz.getY(), xyz.getZ()))));
            });
            selector.prepare(APPOINT, target -> {
                EntrustExecution.tryTemporary(() -> {
                    UUID id = UUID.fromString(target);
                    getServer().getWorlds().forEach(world -> {
                        EntrustExecution.notNull(world.getEntity(id), e -> e.teleport(xyz.getX(), xyz.getY(), xyz.getZ()));
                    });
                }, () -> {
                    EntrustExecution.notNull(getServer().getPlayerManager().getPlayer(target), player -> player.teleport(xyz.getX(), xyz.getY(), xyz.getZ()));
                });
            });
            selector.action();
        });
    }

    @Override
    public boolean supported(String targeted) {
        return ModMdoTriggerBuilder.classMap.containsKey(targeted);
    }
}
