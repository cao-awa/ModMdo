package com.github.cao.awa.modmdo.event.entity.damage;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.cao.awa.modmdo.utils.dimension.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.server.*;
import net.minecraft.world.*;

@Auto
public class EntityDamageEvent extends EntityTargetedEvent<EntityDamageEvent> {
    private final LivingEntity entity;
    private final DamageSource damageSource;
    private final float originalHealth;
    private final float damage;
    private final World world;
    private final MinecraftServer server;

    public EntityDamageEvent(LivingEntity entity, DamageSource damageSource, float originalHealth, float damage, World world, MinecraftServer server) {
        this.entity = entity;
        this.damageSource = damageSource;
        this.originalHealth = originalHealth;
        this.damage = damage;
        this.world = world;
        this.server = server;
    }

    private EntityDamageEvent() {
        this.entity = null;
        this.damageSource = null;
        this.originalHealth = - 1.0F;
        this.damage = - 1.0F;
        this.world = null;
        this.server = null;
    }

    public static EntityDamageEvent snap() {
        return new EntityDamageEvent();
    }

    public ObjectArrayList<LivingEntity> getTargeted() {
        ObjectArrayList<LivingEntity> list = new ObjectArrayList<>();
        list.add(entity);
        return list;
    }

    public DamageSource getDamageSource() {
        return damageSource;
    }

    public float getOriginalHealth() {
        return originalHealth;
    }

    public float getDamage() {
        return damage;
    }

    public World getWorld() {
        return world;
    }

    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public EntityDamageEvent fuse(Previously<EntityDamageEvent> previously, EntityDamageEvent delay) {
        return previously.target();
    }

    public String synopsis() {
        String name = EntrustParser.tryCreate(() -> {
            String str = EntityUtil.getName(entity);
            if (str.equals("")) {
                throw new IllegalArgumentException("empty name");
            }
            return str;
        }, entity.toString());
        return EntrustParser.tryCreate(() -> String.format("EntityDamageEvent{entity=%s, pos=%s, dimension=%s, origin-health=%s, damage=%s}", name, entity.getPos(), DimensionUtil.getDimension(world), originalHealth, damage), toString());
    }

    @Override
    public String abbreviate() {
        return "EntityDamageEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}
