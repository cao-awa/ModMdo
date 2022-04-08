package com.github.zhuaidadaya.modmdo.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modmdo.storage.Variables.timeActive;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method = "setTimeOfDay", at = @At("HEAD"), cancellable = true)
    public void setTimeOfDay(long timeOfDay, CallbackInfo ci) {
        if(!timeActive) {
            ci.cancel();
        }
    }
}