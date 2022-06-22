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
//    @Shadow @Final private MinecraftServer server;
//    @Shadow private boolean inBlockTick;
//    @Shadow
//    @Final
//    private ServerEntityManager<Entity> entityManager;
//
//    @Shadow
//    @Final
//    private EntityList entityList;

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

//    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"))
//    public void cancelBlockE(ServerWorld instance) {
//        if (! testing) {
//            ((WorldInterface)instance).tickBlockEntities();
//        }
//    }
//
//    @Inject(method = "tick", at = @At("HEAD"))
//    public void earlyTickBlockE(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
//        tickBlockE(getThis());
//    }
//
//    public void tickBlockE(ServerWorld instance) {
//        if (testing) {
//            if (blockEntitiesTasks.get(instance) == null) {
//                blockEntitiesTasks.put(instance, new TaskOrder<>(w -> {
//                    EntrustExecution.notNull(w, world -> {
//                        EntrustExecution.tryTemporary(() -> {
//                            ((WorldInterface) world).tickBlockEntities();
//                        });
//                    });
//                }, "tickBlockEntities-" + DimensionUtil.getDimension(instance.getDimension())));
//                blockEntitiesTasks.get(instance).setNoDelay(true);
//            }
//            TaskOrder<ServerWorld> task = blockEntitiesTasks.get(instance);
//
//            task.call(instance);
//        }
//    }
//
//    public ServerWorld getThis() {
//        return (ServerWorld) (WorldInterface) this;
//    }

    //    TODO: 2022/6/22 实体无法使用和方块实体类似的多线程方案
    //    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/EntityList;forEach(Ljava/util/function/Consumer;)V"))
    //    public void cancelE(EntityList instance, Consumer<Entity> action) {
    //    }
    //    public void tickE(EntityList instance, Consumer<Entity> action) {
    //        if (entitiesTasks.get(instance) == null) {
    //            entitiesTasks.put(instance, new TaskOrder<>(list -> {
    //                EntrustExecution.notNull(list, entities -> {
    //                    EntrustExecution.tryTemporary(() -> {
    //                        entities.forEach(action);
    //                    }, Throwable::printStackTrace);
    //                });
    //            }, "tickEntities-" + DimensionUtil.getDimension(getDimension())));
    //            entitiesTasks.get(instance).setNoDelay(true);
    //        }
    //        TaskOrder<EntityList> task = entitiesTasks.get(instance);
    //
    //        if (testing) {
    //            task.call(instance);
    //        } else {
    //            task.enforce(instance);
    //        }
    //    }
    //    @Inject(method = "tick", at = @At("HEAD"))
    //    public void earlyTickE(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
    //        tickE(entityList, (entity) -> {
    //            if (! entity.isRemoved()) {
    //                if (this.shouldCancelSpawn(entity)) {
    //                    entity.discard();
    //                } else {
    //                    getProfiler().push("checkDespawn");
    //                    entity.checkDespawn();
    //                    getProfiler().pop();
    //                    Entity entity2 = entity.getVehicle();
    //                    if (entity2 != null) {
    //                        if (! entity2.isRemoved() && entity2.hasPassenger(entity)) {
    //                            return;
    //                        }
    //
    //                        entity.stopRiding();
    //                    }
    //
    //                    getProfiler().push("tick");
    //                    this.tickEntity(this::tickEntity, entity);
    //                    getProfiler().pop();
    //                }
    //            }
    //        });
    //    }

//    @Shadow
//    protected abstract boolean shouldCancelSpawn(Entity entity);
//
//    @Shadow
//    public abstract void tickEntity(Entity entity);
}
