package com.github.cao.awa.modmdo.mixins.server;

import net.minecraft.server.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    @Final
    private Thread serverThread;

    @Shadow
    public abstract void tickWorlds(BooleanSupplier shouldKeepTicking);

//    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"))
//    public void tickWorlds(MinecraftServer instance, BooleanSupplier shouldKeepTicking) {
//        if (testing) {
//            blockEntitiesTasks.reset();
//            instance.tickWorlds(shouldKeepTicking);
//            blockEntitiesTasks.await();
//        } else {
//            instance.tickWorlds(shouldKeepTicking);
//        }
//    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void delay(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        delayTasks.tick();
        informTask.tick();
    }
}
