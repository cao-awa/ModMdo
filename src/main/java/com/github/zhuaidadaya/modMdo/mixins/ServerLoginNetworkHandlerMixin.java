package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {

    @Shadow @Final public ClientConnection connection;

    /**
     * 如果玩家为null, 则拒绝将玩家添加进服务器
     * (因为其他地方有cancel, 所以可能null)
     *
     * @author 草二号机
     *
     * @param player 玩家
     * @param ci callback
     */
    @Inject(method = "addToServer", at = @At("HEAD"), cancellable = true)
    private void addToServer(ServerPlayerEntity player, CallbackInfo ci) {
        if(player == null)
            ci.cancel();
    }
}