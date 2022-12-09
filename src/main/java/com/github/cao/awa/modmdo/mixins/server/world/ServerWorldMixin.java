package com.github.cao.awa.modmdo.mixins.server.world;

import net.minecraft.server.world.*;
import net.minecraft.util.profiler.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.dimension.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.futureTask;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
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

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickHead(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        // tick tasks
        futureTask.tick();
    }
}
