package com.github.cao.awa.modmdo.mixins.server.world;

import net.minecraft.block.*;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.entity.mob.*;
import net.minecraft.server.world.*;
import net.minecraft.util.function.*;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.*;
import net.minecraft.util.registry.*;
import net.minecraft.util.shape.*;
import net.minecraft.world.*;
import net.minecraft.world.dimension.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;
import java.util.function.*;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World {
    @Shadow public abstract ServerChunkManager getChunkManager();

    @Shadow @Final private Set<MobEntity> loadedMobs;

    //    private static final Method tickBlockEntities = EntrustParser.create(() -> {
//        return EntrustParser.tryCreate(() -> {
//            return World.class.getDeclaredMethod("tickBlockEntities");
//        }, null);
//    });
//
//    @Shadow
//    @Final
//    EntityList entityList;
//    @Shadow
//    @Final
//    private MinecraftServer server;
//    @Shadow
//    private boolean inBlockTick;
//    @Shadow
//    @Final
//    private ServerEntityManager<Entity> entityManager;
//
//    private ServerWorld self;
//
    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DimensionType dimensionType, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, dimensionType, profiler, isClient, debugWorld, seed);
    }
//
//    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"))
//    public void vanillaBlockEntityTick(ServerWorld instance) {
//        self = instance;
//        // redirect this method for cancel vanilla tick, but vanilla still usable
//        if (config.getConfigBoolean("vanilla_block_entity_ticker", true)) {
//            EntrustExecution.tryTemporary(() -> tickBlockEntities.invoke(instance));
//        }
//    }
//
//    @Inject(method = "tick", at = @At("HEAD"))
//    public void earlyTickBlockE(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
//        if (! config.getConfigBoolean("vanilla_block_entity_ticker", true)) {
//            modmdoTickBlockEntity(self());
//        }
//    }
//
//    public synchronized void modmdoTickBlockEntity(ServerWorld instance) {
//        if (testing) {
//            if (blockEntitiesTasks.get(instance) == null) {
//                blockEntitiesTasks.put(instance, new TaskOrder<>(world -> {
//                    EntrustExecution.tryTemporary(() -> tickBlockEntities.invoke(world));
//                    blockEntitiesTasks.done(world);
//                }, "tile_ticker: " + DimensionUtil.getDimension(instance.getDimension())));
//                blockEntitiesTasks.get(instance).setNoDelay(true);
//            }
//            blockEntitiesTasks.participate(instance, (world, task) -> task.call(world));
//        } else {
//            EntrustExecution.tryTemporary(() -> tickBlockEntities.invoke(instance));
//        }
//    }
//
//    public ServerWorld self() {
//        return self;
//    }

    @Inject(method = "updateListeners", at = @At("HEAD"), cancellable = true)
    public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        this.getChunkManager().markForUpdate(pos);
        VoxelShape voxelShape = oldState.getCollisionShape(this, pos);
        VoxelShape voxelShape2 = newState.getCollisionShape(this, pos);
        if (VoxelShapes.matchesAnywhere(voxelShape, voxelShape2, BooleanBiFunction.NOT_SAME)) {
            loadedMobs.stream().forEach(mobEntity -> {
                EntityNavigation entityNavigation = mobEntity.getNavigation();
                if (! entityNavigation.shouldRecalculatePath()) {
                    entityNavigation.onBlockChanged(pos);
                }
            });
        }
        ci.cancel();
    }
}
