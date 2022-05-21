package com.github.zhuaidadaya.modmdo.mixins.tracker;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {
    @Shadow @Final private LivingEntity entity;

    @Inject(method = "onDamage", at = @At("HEAD"))
    public void onDamage(DamageSource damageSource, float originalHealth, float damage, CallbackInfo ci) {
        if (extras != null && extras.isActive(EXTRA_ID)) {
            event.submitEntityDamage(entity, damageSource, originalHealth, damage, entity.getEntityWorld(), entity.getServer());
        }
    }
}
