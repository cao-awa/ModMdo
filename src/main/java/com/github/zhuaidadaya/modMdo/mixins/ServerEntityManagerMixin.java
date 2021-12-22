package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.server.world.ServerEntityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modMdo.storage.Variables.enableTickAnalyzer;
import static com.github.zhuaidadaya.modMdo.storage.Variables.tickMap;

@Mixin(ServerEntityManager.class)
public class ServerEntityManagerMixin {
    @Inject(method = "loadChunks",at = @At("HEAD"))
    public void loadChunksStart(CallbackInfo ci) {
        if(enableTickAnalyzer) {
            tickMap.put("tick_world" + tickMap.get("ticking_world") + "_entities_load_chunk_start", System.currentTimeMillis());
        }
    }


    @Inject(method = "loadChunks",at = @At("RETURN"))
    public void loadChunksEnd(CallbackInfo ci) {
        if(enableTickAnalyzer) {
            tickMap.put("tick_world" + tickMap.get("ticking_world") + "_entities_load_chunk_time", tickMap.get("tick_world" +tickMap.get("ticking_world") + "_entities_load_chunk_start") - System.currentTimeMillis());
        }
    }

    @Inject(method = "unloadChunks",at = @At("HEAD"))
    public void unloadChunksStart(CallbackInfo ci) {
        if(enableTickAnalyzer) {
            tickMap.put("tick_world" + tickMap.get("ticking_world") + "_entities_unload_chunk_start", System.currentTimeMillis());
        }
    }


    @Inject(method = "unloadChunks",at = @At("RETURN"))
    public void unloadChunksEnd(CallbackInfo ci) {
        if(enableTickAnalyzer) {
            tickMap.put("tick_world" + tickMap.get("ticking_world") +"_entities_unload_chunk_time", System.currentTimeMillis() - tickMap.get("tick_world" + tickMap.get("ticking_world") + "_entities_unload_chunk_start"));
        }
    }
}
