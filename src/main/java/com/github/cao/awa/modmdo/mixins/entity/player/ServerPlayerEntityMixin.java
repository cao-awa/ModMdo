package com.github.cao.awa.modmdo.mixins.entity.player;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.server.network.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow public abstract Entity getCameraEntity();

    @Shadow private Entity cameraEntity;

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeath(DamageSource source, CallbackInfo ci) {
        cameraEntity = null;
        if (getCameraEntity() instanceof ServerPlayerEntity entity) {
            event.submitEntityDeath(entity, entity.getDamageTracker().getBiggestAttacker(), entity.getPos(), entity.getServer());
        }
    }
}
