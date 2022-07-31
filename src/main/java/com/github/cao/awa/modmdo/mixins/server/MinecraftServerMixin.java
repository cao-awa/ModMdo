package com.github.cao.awa.modmdo.mixins.server;

import net.minecraft.server.*;
import org.spongepowered.asm.mixin.*;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
//    @Shadow
//    @Final
//    private Thread serverThread;
//
//    @Shadow
//    public abstract void tickWorlds(BooleanSupplier shouldKeepTicking);
//
//    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"))
//    public void tick(MinecraftServer instance, BooleanSupplier shouldKeepTicking) {
//        instance.tickWorlds(shouldKeepTicking);
//        if (testing) {
//            while (running()) {
//                EntrustExecution.tryTemporary(() -> {
//                    TimeUnit.NANOSECONDS.sleep(5000);
//                });
//            }
//        }
//    }
//
//    public boolean running() {
//        return EntrustParser.trying(() -> {
//            for (TaskOrder<ServerWorld> task : blockEntitiesTasks.values()) {
//                if (task.isRunning()) {
//                    return true;
//                }
//            }
//            //     TODO: 2022/6/22 实体无法使用和方块实体类似的多线程方案
//            //     for (TaskOrder<EntityList> task : entitiesTasks.values()) {
//            //         if (task.isRunning()) {
//            //             return true;
//            //         }
//            //     }
//            return false;
//        }, ex -> false);
//    }
}
