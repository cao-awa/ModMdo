package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * TAG:DRT|SKP|VSD
 * 这个tag用于注明这是有版本差异的
 * 存在这个tag时不会直接从其他正在开发的部分复制
 * 而是手动替换
 * TAG:
 * DRT(Don't Replace It)
 * SKP(Skip)
 * VSD(Version Difference)
 * <p>
 * 手动替换检测: 1.17.x
 */
@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {

    @Shadow
    protected abstract void sendChunkDataPackets(ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk);

    /**
     * 延迟区块发送的时间, 这个区块数据在检查超时后才发送
     * 并且到时间后再次确定是否已经通过检查
     * 以此达到不通过检查就不允许交互服务器数据的目的
     * <p>
     * 这么做虽然不是非常高性能, 但是以后会修改的
     *
     * @param player
     *         数据发往的玩家
     * @param packets
     *         不知道是什么的包
     * @param chunk
     *         区块数据
     * @param ci
     *         callback
     *
     * @author 草awa
     * @author zhuaidadaya
     * @author 草二号机
     */
//    @Inject(method = "sendChunkDataPackets", at = @At("HEAD"), cancellable = true)
//    public void sendChunkDataPackets(ServerPlayerEntity player, Packet<?>[] packets, WorldChunk chunk, CallbackInfo ci) {
//        if(playersChunkSendCache.contains(player))
//            ci.cancel();
//
//        if(! loginUsers.hasUser(player)) {
//            playersChunkSendCache.add(player);
//
//            new Thread(() -> {
//                try {
//                    Thread.sleep(1000);
//
//                    if(server.getPlayerManager().getPlayerList().contains(player) & loginUsers.hasUser(player)) {
//                        sendChunkDataPackets(player, packets, chunk);
//                    } else {
//                        player.networkHandler.sendPacket(new DisconnectS2CPacket(new LiteralText("failed to process data")));
//                        player.networkHandler.disconnect(new LiteralText("failed to process data"));
//                    }
//
//                } catch (InterruptedException e) {
//
//                }
//
//                playersChunkSendCache.remove(player);
//            }).start();
//
//            ci.cancel();
//        }
//    }
}