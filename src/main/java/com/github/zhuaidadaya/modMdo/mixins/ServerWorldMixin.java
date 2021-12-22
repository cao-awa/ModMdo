package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

import static com.github.zhuaidadaya.modMdo.storage.Variables.enableTickAnalyzer;
import static com.github.zhuaidadaya.modMdo.storage.Variables.tickMap;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Inject(method = "tick",at = @At("HEAD"))
    public void tickWorldStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(enableTickAnalyzer) {
            try {
                tickMap.put("tick_world" + tickMap.get("ticking_world") + "_start", System.currentTimeMillis());
            } catch (Exception e) {

            }
        }
    }

    @Inject(method = "tick",at = @At("RETURN"))
    public void tickWorldEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(enableTickAnalyzer) {
            try {
                tickMap.put("tick_world" + tickMap.get("ticking_world") + "_time", System.currentTimeMillis() - tickMap.get("tick_world" + tickMap.get("ticking_world") + "_start"));
                tickMap.put("ticking_world", tickMap.get("ticking_world") + 1);
            } catch (Exception e) {

            }
        }
    }
}
