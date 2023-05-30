package com.github.cao.awa.modmdo.mixins.server.world;

import net.minecraft.server.world.*;
import net.minecraft.util.profiler.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.dimension.*;
import org.spongepowered.asm.mixin.*;

import java.util.function.*;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(
                properties,
                registryRef,
                dimensionType,
                profiler,
                isClient,
                debugWorld,
                seed
        );
    }
}
