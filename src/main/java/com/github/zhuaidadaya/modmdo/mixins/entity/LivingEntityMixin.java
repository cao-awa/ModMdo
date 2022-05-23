package com.github.zhuaidadaya.modmdo.mixins.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.event;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract DamageTracker getDamageTracker();

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        Entity self = getDamageTracker().getEntity();
        if (self instanceof LivingEntity entity) {
            event.submitEntityDeath(entity, getDamageTracker().getBiggestAttacker(), getPos(), getServer());
        }
    }
}
