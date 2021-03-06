package com.github.cao.awa.modmdo.mixins.entity;

import com.github.cao.awa.modmdo.event.entity.death.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        Entity self = getDamageTracker().getEntity();
        if (self instanceof LivingEntity entity) {
            event.submit(new EntityDeathEvent(entity, getDamageTracker().getBiggestAttacker(), getPos(), getServer()));
        }
    }

    @Shadow
    public abstract DamageTracker getDamageTracker();
}
