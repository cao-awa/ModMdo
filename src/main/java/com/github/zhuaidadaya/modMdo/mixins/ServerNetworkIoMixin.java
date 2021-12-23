package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.server.ServerNetworkIo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modMdo.storage.Variables.enableTickAnalyzer;
import static com.github.zhuaidadaya.modMdo.storage.Variables.tickMap;

@Mixin(ServerNetworkIo.class)
public class ServerNetworkIoMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickNetworkStart(CallbackInfo ci) {
        if(enableTickAnalyzer) {
            tickMap.put("tick_network_start", System.nanoTime());
        }
    }


    @Inject(method = "tick", at = @At("RETURN"))
    public void tickNetworkEnd(CallbackInfo ci) {
        if(enableTickAnalyzer) {
            tickMap.put("tick_network_time", System.nanoTime() - tickMap.get("tick_network_start"));
        }
    }
}
