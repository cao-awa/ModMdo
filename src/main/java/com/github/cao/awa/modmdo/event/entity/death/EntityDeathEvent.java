package com.github.cao.awa.modmdo.event.entity.death;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;

@Auto
public class EntityDeathEvent extends EntityTargetedEvent<EntityDeathEvent> {
    private final LivingEntity entity;
    private final Vec3d pos;
    private final MinecraftServer server;
    private final LivingEntity perpetrator;

    public EntityDeathEvent(LivingEntity entity, LivingEntity perpetrator, Vec3d pos, MinecraftServer server) {
        this.entity = entity;
        this.pos = pos;
        this.server = server;
        this.perpetrator = perpetrator;
    }

    private EntityDeathEvent() {
        this.entity = null;
        this.pos = null;
        this.server = null;
        this.perpetrator = null;
    }

    @Override
    public String getName() {
        return "EntityDeath";
    }

    public static EntityDeathEvent snap() {
        return new EntityDeathEvent();
    }

    public ObjectArrayList<LivingEntity> getTargeted() {
        ObjectArrayList<LivingEntity> list = new ObjectArrayList<>();
        list.add(entity);
        return list;
    }

    public Vec3d getPos() {
        return pos;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public EntityDeathEvent fuse(Previously<EntityDeathEvent> previously, EntityDeathEvent delay) {
        return previously.target();
    }

    @Override
    public String abbreviate() {
        return "EntityDeathEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}
