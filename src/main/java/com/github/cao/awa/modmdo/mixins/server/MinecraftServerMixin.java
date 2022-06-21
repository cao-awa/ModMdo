package com.github.cao.awa.modmdo.mixins.server;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Shadow @Final private Thread serverThread;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeNano()J"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/util/snooper/Snooper;update()V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/util/MetricsData;pushSample(J)V")))
    public long tick() {
        while (tickBlockEntitiesTask.isRunning()) {
            EntrustExecution.tryTemporary(() -> {
                Thread.sleep(1);
            }, Throwable::printStackTrace);
        }
        return Util.getMeasuringTimeNano();
    }
}
