package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.modMdo.login.token.TokenContentType;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {
    @Shadow
    @Final
    private ClientConnection connection;

    @Shadow @Final private GameProfile profile;

    /**
     * 如果收到了服务器的包,确定对方是一个ModMdo服务器并开启Token加密才发送数据包
     * 如果服务器未安装ModMdo或未开启加密, 则不发送
     *
     * @param packet
     *         packet
     * @param ci
     *         callback
     *
     * @author 草二号机
     * @author 草
     * @author zhuaidadaya
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onOnCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        PacketByteBuf data = packet.getData();

        try {
            int id;
            id = data.readVarInt();

            if(id == 99) {
                String address = formatAddress(connection.getAddress());
                String token = getModMdoTokenFormat(address, TokenContentType.TOKEN_BY_ENCRYPTION);
                String loginType = getModMdoTokenFormat(address, TokenContentType.LOGIN_TYPE);
                UUID uuid = PlayerEntity.getUuidFromProfile(profile);
                connection.send(new CustomPayloadC2SPacket(tokenChannel, (new PacketByteBuf(Unpooled.buffer())).writeString(uuid.toString()).writeString(profile.getName()).writeString(loginType).writeString(token).writeString(address).writeString(String.valueOf(MODMDO_VERSION))));
            }
        } catch (Exception e) {
            LOGGER.error("error in connecting ModMdo server", e);
        }
        ci.cancel();
    }
}