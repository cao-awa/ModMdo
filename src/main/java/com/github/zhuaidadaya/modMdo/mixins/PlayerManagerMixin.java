package com.github.zhuaidadaya.modMdo.mixins;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    @Shadow
    @Final
    private Map<UUID, ServerPlayerEntity> playerMap;

    /**
     * 当相同的玩家在线时, 禁止重复创建玩家
     * 几乎解决了玩家异地登录下线的问题
     *
     * @author 草二号机
     * @param profile 即将加入的玩家
     * @param cir callback
     */
    @Inject(method = "createPlayer",at = @At("HEAD"))
    public void createPlayer(GameProfile profile, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        if(enableRejectReconnect) {
            UUID uUID = PlayerEntity.getUuidFromProfile(profile);
            for(ServerPlayerEntity serverPlayerEntity : this.players) {
                if(serverPlayerEntity.getUuid().equals(uUID))
                    cir.cancel();
            }
        }
    }

    /**
     * 给玩家发送是否开启加密token
     * 避免在不开启时发送token消耗网络带宽或流量
     * (虽然不会占很多)
     *
     * @author 草二号机
     * @author 草
     *
     * @param connection 玩家的连接
     * @param player 玩家
     * @param ci callback
     */
    @Inject(method = "onPlayerConnect",at = @At("HEAD"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        connection.send(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(enableEncryptionToken ? 99 : 96)));
    }
}