package com.github.cao.awa.modmdo.mixins.server;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.*;
import net.minecraft.server.*;
import net.minecraft.server.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    @Final
    private Thread serverThread;

    @Shadow
    public abstract void tickWorlds(BooleanSupplier shouldKeepTicking);

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"))
    public void tick(MinecraftServer instance, BooleanSupplier shouldKeepTicking) {
        instance.tickWorlds(shouldKeepTicking);
        while (running()) {
            EntrustExecution.tryTemporary(() -> {
                Thread.sleep(0, 5000);
            });
        }
    }

    public boolean running() {
        if (!testing) {
            return false;
        }
        return EntrustParser.trying(() -> {
            for (TaskOrder<ServerWorld> task : blockEntitiesTasks.values()) {
                if (task.isRunning()) {
                    return true;
                }
            }
            //     TODO: 2022/6/22 实体无法使用和方块实体类似的多线程方案
            //     for (TaskOrder<EntityList> task : entitiesTasks.values()) {
            //         if (task.isRunning()) {
            //             return true;
            //         }
            //     }
            return false;
        }, ex -> false);
    }
}
