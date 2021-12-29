package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.MCH.times.TimeType;
import com.github.zhuaidadaya.MCH.times.Times;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow
    @Final
    private Map<RegistryKey<World>, ServerWorld> worlds;

    @Shadow private float tickTime;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tickStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(enabledCancelTIck) {
            if(cancelTickStart > 60000)
                enabledCancelTIck = false;
            ci.cancel();
        }

        if(enableTickAnalyzer) {
            tickMap.put("tick_start", System.nanoTime());
            tickStartTime = Times.getTime(TimeType.ALL);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(enableTickAnalyzer) {
            StringBuilder result = new StringBuilder();
            try {
                tickMap.put("tick_time", System.nanoTime() - tickMap.get("tick_start"));
                long baseTickStart = tickMap.get("tick_start");
                result.append("tick_").append(baseTickStart).append("(").append(tickStartTime).append(")").append("\n");
                result.append("tick: ").append(tickMap.get("tick_time")).append("ns").append("\n");
                result.append("|--tick world: ").append(tickMap.get("tick_worlds_time")).append("ns").append("\n");
                result.append("|    |--step start: ").append(tickMap.get("tick_worlds_start") - baseTickStart).append("ns*").append("\n");
                result.append("|    |--worlds: ").append(worlds.size()).append("\n");
                for(int i = 1; i < worlds.size() + 1; i++) {
                    String name = "unknown";
                    switch(i) {
                        case 1 -> name = "overworld?";
                        case 2 -> name = "the_nether?";
                        case 3 -> name = "the_end?";
                    }

                    try {
                        long tickWorldTime = - 1;
                        try {
                            tickWorldTime = tickMap.get("tick_world" + i + "_time");
                        } catch (Exception e) {

                        }
                        long tickWorldStart = baseTickStart;

                        try {
                            tickWorldStart = tickMap.get("tick_world" + i + "_start");
                        } catch (Exception e) {

                        }
                        long tickEntitiesTime = - 1;

                        try {
                            tickEntitiesTime = tickMap.get("tick_world" + i + "_entities_time");
                        } catch (Exception e) {

                        }
                        long tickEntitiesStart = baseTickStart;

                        try {
                            tickEntitiesStart = tickMap.get("tick_world" + i + "_entities_start");
                        } catch (Exception e) {

                        }
                        long tickEntitiesLoadChunksTime = - 1;
                        try {
                            tickEntitiesLoadChunksTime = tickMap.get("tick_world" + i + "_entities_load_chunk_time");
                        } catch (Exception e) {

                        }
                        long tickEntitiesLoadChunksStart = baseTickStart;
                        try {
                            tickEntitiesLoadChunksStart = tickMap.get("tick_world" + i + "_entities_load_chunk_start");
                        } catch (Exception e) {

                        }
                        long tickEntitiesUnloadChunksTime = - 1;
                        try {
                            tickEntitiesUnloadChunksTime = tickMap.get("tick_world" + i + "_entities_unload_chunk_time");
                        } catch (Exception e) {

                        }
                        long tickEntitiesUnloadChunksStart = baseTickStart;
                        try {
                            tickEntitiesUnloadChunksStart = tickMap.get("tick_world" + i + "_entities_unload_chunk_start");
                        } catch (Exception e) {

                        }
                        long tickWorldChunksManagerTime = - 1;
                        try {
                            tickWorldChunksManagerTime = tickMap.get("tick_world" + i + "_chunks_manager_time");
                        } catch (Exception e) {

                        }
                        long tickWorldChunksManagerStart = baseTickStart;
                        try {
                            tickWorldChunksManagerStart = tickMap.get("tick_world" + i + "_chunks_manager_start");
                        } catch (Exception e) {

                        }
                        long tickWorldChunksTime = - 1;
                        try {
                            tickWorldChunksTime = tickMap.get("tick_world" + i + "_chunks_time");
                        } catch (Exception e) {

                        }
                        long tickWorldChunksStart = baseTickStart;
                        try {
                            tickWorldChunksStart = tickMap.get("tick_world" + i + "_chunks_start");
                        } catch (Exception e) {

                        }
                        long tickEntitiesManagerTime = - 1;
                        try {
                            tickEntitiesManagerTime = tickMap.get("tick_world" + i + "_entities_manager_time");
                        } catch (Exception e) {

                        }
                        long tickEntitiesManagerStart = baseTickStart;
                        try {
                            tickEntitiesManagerStart = tickMap.get("tick_world" + i + "_entities_manager_start");
                        } catch (Exception e) {

                        }
                        if(tickWorldTime < 0) {
                            throw new Exception();
                        }
                        long entitiesCount = 0;
                        try {
                            entitiesCount = tickMap.get("world" + i + "_entities");
                        } catch (Exception e) {

                        }
                        result.append("|    |    |--world").append(i).append("(").append(name).append("): ").append(tickWorldTime).append("ns").append("\n");
                        result.append("|    |    |    |--step start: ").append(tickWorldStart - baseTickStart).append("ns*").append("\n");
                        result.append("|    |    |    |--tick chunks(M): ").append(tickWorldChunksManagerTime).append("ns").append("\n");
                        result.append("|    |    |    |    |--step start: ").append(tickWorldChunksManagerStart - baseTickStart).append("ns*").append("\n");
                        result.append("|    |    |    |    |--tick chunks: ").append(tickWorldChunksTime).append("ns").append("\n");
                        result.append("|    |    |    |    |    |--step time: ").append(tickWorldChunksStart - baseTickStart).append("ns*").append("\n");
                        result.append("|    |    |    |--tick entities(W): ").append(tickEntitiesTime).append("ns").append("\n");
                        result.append("|    |    |    |    |--step start: ").append(tickEntitiesStart - baseTickStart).append("ns*").append("\n");
                        result.append("|    |    |    |    |--entities: ").append(entitiesCount).append("\n");
                        if(entitiesCount > 0) {
                            try {
                                LinkedHashMap<String, Integer> entities = tickEntitiesMap.get("world" + i + "_entities");
                                for(String s : entities.keySet()) {
                                    result.append("|    |    |    |    |    |--").append(s).append(": ").append(entities.get(s)).append("\n");
                                }
                            } catch (Exception e) {

                            }
                        }
                        result.append("|    |    |    |--tick entities(M): ").append(tickEntitiesManagerTime).append("ns").append("\n");
                        result.append("|    |    |    |    |--step start: ").append(tickEntitiesManagerStart - baseTickStart).append("ns*").append("\n");
                        result.append("|    |    |    |    |--load chunks(M): ").append(tickEntitiesLoadChunksTime).append("ns").append("\n");
                        result.append("|    |    |    |    |    |--step start: ").append(tickEntitiesLoadChunksStart - baseTickStart).append("ns*").append("\n");
                        result.append("|    |    |    |    |--unload chunks(M): ").append(tickEntitiesUnloadChunksTime).append("ns").append("\n");
                        result.append("|    |    |    |    |    |--step start: ").append(tickEntitiesUnloadChunksStart - baseTickStart).append("ns*").append("\n");
                    } catch (Exception e) {
                        result.append("|    |    |--world").append(i).append("(unknown): 0ns").append("\n");
                    }
                }
                result.append("|--network IO: ").append(tickMap.get("tick_network_time")).append("ns").append("\n");
                result.append("|    |--step start: ").append(tickMap.get("tick_network_start") - baseTickStart).append("ns*").append("\n");
            } catch (Exception e) {
                result = new StringBuilder();
                result.append("bad_tick").append("(").append(tickStartTime).append(")").append("\n");
            }

            try {
                new File(tickAnalyzerFile).getParentFile().mkdirs();
                BufferedWriter writer = new BufferedWriter(new FileWriter(tickAnalyzerFile, true));
                writer.write(result.toString());
                writer.close();
            } catch (Exception e) {

            }

            analyzedTick++;

            if(analyzedTick % 10 == 0)
                LOGGER.info("snap tick(" + analyzedTick + "tick), cached time analyzed");

            if(shortAnalyze & analyzedTick > 59) {
                enableTickAnalyzer = false;

                LOGGER.info("tick analyze finished, result at " + tickAnalyzerFile);
            }
        }
    }

    @Inject(method = "tickWorlds", at = @At("HEAD"))
    public void tickWorldsStart(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(enableTickAnalyzer) {
            try {
                tickMap.put("tick_worlds_start", System.nanoTime());
                tickMap.put("ticking_world", 1L);
                tickEntitiesMap = new LinkedHashMap<>();
            } catch (Exception e) {

            }
        }
    }

    @Inject(method = "tickWorlds", at = @At("RETURN"))
    public void tickWorldsEnd(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if(enableTickAnalyzer) {
            try {
                tickMap.put("tick_worlds_time", System.nanoTime() - tickMap.get("tick_worlds_start"));
            } catch (Exception e) {

            }
        }
    }
}
