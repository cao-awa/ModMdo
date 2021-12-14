package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modMdo.storage.Variables.loginUsers;
import static com.github.zhuaidadaya.modMdo.storage.Variables.server;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {

    @Shadow
    protected abstract void sendChunkDataPackets(ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk);

    @Inject(method = "sendChunkDataPackets", at = @At("HEAD"), cancellable = true)
    public void sendChunkDataPackets(ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk, CallbackInfo ci) {
        if(! loginUsers.hasUser(player)) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);

                    if(server.getPlayerManager().getPlayerList().contains(player) & loginUsers.hasUser(player)) {
                        sendChunkDataPackets(player, packets, chunk);
                    }

                } catch (InterruptedException e) {

                }
            }).start();

            ci.cancel();
        }
    }
}