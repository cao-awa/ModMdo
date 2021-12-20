package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.modMdo.login.token.TokenContentType;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    @Final
    private ClientConnection connection;

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
            int id = data.readVarInt();
            if(id == 99)
                enableEncryptionToken = true;
            if(id == 96)
                enableEncryptionToken = false;
        } catch (Exception e) {

        }
        ci.cancel();
    }

    /**
     * 在加入游戏时发送校验数据包
     * <p>
     * 正在研究加入游戏前的数据包发送
     * 许多尝试的结果都是NullPointerException
     * 稍微需要一点时间吃透源码
     *
     * @param packet
     *         packet
     * @param ci
     *         callback
     *
     * @author 草awa
     * @author 草二号机
     * @author zhuaidadaya
     */
    @Inject(method = "onGameJoin", at = @At("RETURN"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if(enableEncryptionToken) {
            String address = formatAddress(connection.getAddress());
            String token = getModMdoTokenFormat(address, TokenContentType.TOKEN_BY_ENCRYPTION);
            String loginType = getModMdoTokenFormat(address, TokenContentType.LOGIN_TYPE);
            client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(tokenChannel, (new PacketByteBuf(Unpooled.buffer())).writeString(client.player.getUuid().toString()).writeString(client.player.getName().asString()).writeString(loginType).writeString(token).writeString(address)));
        }
    }
}