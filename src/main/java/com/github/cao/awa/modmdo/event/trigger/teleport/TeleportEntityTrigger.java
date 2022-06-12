package com.github.cao.awa.modmdo.event.trigger.teleport;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.selector.entity.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import com.github.cao.awa.modmdo.simple.vec.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.list.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import org.json.*;

@Auto
public class TeleportEntityTrigger<T extends EntityTargetedEvent<?>> extends TargetedTrigger<T> {
    private EntitySelectorType selector = EntitySelectorType.SELF;
    private XYZ xyz;

    @Override
    public ModMdoEventTrigger<T> build(T event, JSONObject metadata, TriggerTrace trace) {
        setMeta(metadata);
        setTarget(event.getTargeted());
        setServer(getTarget().get(0).getServer());
        setTrace(trace);
        selector = EntitySelectorType.of(metadata.getString("selector"));
        xyz = new XYZ(metadata.getJSONObject("pos"));
        return this;
    }

    @Override
    public void action() {
        EntrustExecution.tryTemporary(() -> {
            switch (selector) {
                case SELF -> {
                    if (getTarget().size() > 1) {
                        err("Cannot use \"SELF\" selector in targeted more than one", new IllegalArgumentException("Unable to process \"SELF\" selector for entities, it need appoint an entity"));
                        return;
                    }
                    getTarget().get(0).teleport(xyz.getX(), xyz.getY(),xyz.getZ());
                }
                case WORLD -> EntrustExecution.notNull(getServer().getWorld(getTarget().get(0).world.getRegistryKey()), world -> world.iterateEntities().forEach(entity -> entity.teleport(xyz.getX(), xyz.getY(),xyz.getZ())));
                case ALL -> getServer().getWorlds().forEach(world -> world.iterateEntities().forEach(entity -> entity.teleport(xyz.getX(), xyz.getY(),xyz.getZ())));
                case APPOINT -> EntrustExecution.notNull(getServer().getPlayerManager().getPlayer(getMeta().has("name") ? getMeta().getString("name") : getMeta().getString("uuid")), entity -> entity.teleport(xyz.getX(), xyz.getY(),xyz.getZ()));
            }
        }, Throwable::printStackTrace);
    }

    @Override
    public UnmodifiableListReceptacle<String> supported() {
        return new UnmodifiableListReceptacle<>(EntrustParser.operation(new ObjectArrayList<>(), list -> {
            list.addAll(ModMdoTriggerBuilder.classMap.values());
        }));
    }
}
