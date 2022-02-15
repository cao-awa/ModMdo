package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modMdo.storage.Variables.timeActive;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "setTimeOfDay", at = @At("HEAD"), cancellable = true)
    public void setTimeOfDay(long timeOfDay, CallbackInfo ci) {
        if(!timeActive) {
            ci.cancel();
        }
    }
}