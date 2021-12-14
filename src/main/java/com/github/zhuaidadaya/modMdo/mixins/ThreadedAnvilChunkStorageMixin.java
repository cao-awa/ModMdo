package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modMdo.storage.Variables.loginUsers;
import static com.github.zhuaidadaya.modMdo.storage.Variables.server;

@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {

    @Shadow protected abstract void sendChunkDataPackets(ServerPlayerEntity player, MutableObject<ChunkDataS2CPacket> cachedDataPacket, WorldChunk chunk);

    @Inject(method = "sendChunkDataPackets",at = @At("HEAD"), cancellable = true)
    private void sendChunkDataPackets(ServerPlayerEntity player, MutableObject<ChunkDataS2CPacket> cachedDataPacket, WorldChunk chunk, CallbackInfo ci) {
        if(! loginUsers.hasUser(player)) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);

                    if(server.getPlayerManager().getPlayerList().contains(player) & loginUsers.hasUser(player)) {
                        sendChunkDataPackets(player, cachedDataPacket, chunk);
                    }

                } catch (InterruptedException e) {

                }
            }).start();

            ci.cancel();
        }
    }
}