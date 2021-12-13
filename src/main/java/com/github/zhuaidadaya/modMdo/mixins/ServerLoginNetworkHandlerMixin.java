package com.github.zhuaidadaya.modMdo.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow private @Nullable GameProfile profile;

    @Shadow protected abstract GameProfile toOfflineProfile(GameProfile profile);

    @Shadow @Final public ClientConnection connection;

    @Shadow public abstract void disconnect(Text reason);

    @Inject(method = "addToServer", at = @At("HEAD"), cancellable = true)
    private void addToServer(ServerPlayerEntity player, CallbackInfo ci) {
        if(player == null)
            ci.cancel();
    }
}