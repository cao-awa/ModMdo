package com.github.cao.awa.modmdo.mixins.tracker;

import com.github.cao.awa.modmdo.event.entity.damage.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {
    @Shadow
    @Final
    private LivingEntity entity;

    @Inject(method = "onDamage", at = @At("HEAD"))
    public void onDamage(DamageSource damageSource, float originalHealth, float damage, CallbackInfo ci) {
        event.submit(new EntityDamageEvent(entity, damageSource, originalHealth, damage, entity.getEntityWorld(), entity.getServer()));
    }
}
