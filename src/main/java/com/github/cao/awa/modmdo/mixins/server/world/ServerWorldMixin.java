package com.github.cao.awa.modmdo.mixins.server.world;

import net.minecraft.registry.*;
import net.minecraft.registry.entry.*;
import net.minecraft.server.world.*;
import net.minecraft.util.profiler.*;
import net.minecraft.world.*;
import net.minecraft.world.dimension.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(
                properties,
                registryRef,
                dimension,
                profiler,
                isClient,
                debugWorld,
                seed,
                maxChainedNeighborUpdates
        );
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHead(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        // tick tasks
        futureTask.tick();
    }
}
