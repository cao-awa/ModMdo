package com.github.cao.awa.modmdo.event.entity.death;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.utils.dimension.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
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

    public String synopsis() {
        String name = EntrustParser.trying(() -> EntrustParser.tryCreate(() -> {
             String str = entity.getDisplayName().asString();
             if (str.equals("")) {
                 throw new IllegalArgumentException("empty name");
             }
             return str;
         }, entity.toString()), () -> "null");
        String perpetratorName = EntrustParser.trying(() -> EntrustParser.tryCreate(() -> {
            String str = perpetrator.getDisplayName().asString();
            if (str.equals("")) {
                throw new IllegalArgumentException("empty name");
            }
            return str;
        }, perpetrator.toString()), () -> "null");
        return EntrustParser.tryCreate(() -> String.format("EntityDeathEvent{player=%s, perpetrator=%s, pos=%s, dimension=%s}", name, perpetratorName, pos, DimensionUtil.getDimension(entity.getEntityWorld().getDimension())), toString());
    }

    @Override
    public String abbreviate() {
        return "EntityDeathEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}
