package com.github.cao.awa.modmdo.mixins.server.world;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Shadow @Final private List<ServerPlayerEntity> players;
    private static final TaskOrder<ServerWorld> task = tickBlockEntitiesTask;

    {
        task.setNoDelay(true);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tickBlockEntities()V"))
    public void tickBlockE(ServerWorld instance) {
        if (testing) {
            task.call(instance);
        } else {
            task.enforce(instance);
        }
    }
}
