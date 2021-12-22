package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

import static com.github.zhuaidadaya.modMdo.storage.Variables.enableTickAnalyzer;
import static com.github.zhuaidadaya.modMdo.storage.Variables.tickMap;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {

    @Shadow @Final public ThreadedAnvilChunkStorage threadedAnvilChunkStorage;

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At("HEAD"))
    public void tickChunkStart(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if(enableTickAnalyzer) {
            try {
                tickMap.put("tick_world" + tickMap.get("ticking_world") + "_chunks_start", System.currentTimeMillis());

            } catch (Exception e) {

            }
        }
    }

    @Inject(method = "tick(Ljava/util/function/BooleanSupplier;)V",at = @At("RETURN"))
    public void tickChunkEnd(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        if(enableTickAnalyzer) {
            try {
                tickMap.put("tick_world" + tickMap.get("ticking_world") + "_chunks_time", System.currentTimeMillis() - tickMap.get("tick_world" + tickMap.get("ticking_world") + "_chunks_start"));
            } catch (Exception e) {

            }
        }
    }
}
