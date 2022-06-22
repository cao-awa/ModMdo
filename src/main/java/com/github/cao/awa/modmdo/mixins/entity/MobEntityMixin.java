package com.github.cao.awa.modmdo.mixins.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
}
