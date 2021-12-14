package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.modMdo.token.TokenContentType;
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

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onOnCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        PacketByteBuf data = packet.getData();

        try {
            if(data != null) {
                int id = data.readVarInt();
                if(id == 99)
                    enableEncryptionToken = true;
                if(id == 96)
                    enableEncryptionToken = false;
            }
        } catch (Exception e) {

        }
        ci.cancel();
    }

    @Inject(method = "onGameJoin", at = @At("RETURN"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if(enableEncryptionToken) {
            String address = connection.getAddress().toString();
            address = address.substring(0, address.indexOf("/")) + ":" + address.substring(address.lastIndexOf(":") + 1);
            client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(connectingChannel, (new PacketByteBuf(Unpooled.buffer())).writeString(client.player.getUuid().toString()).writeString(client.player.getName().asString())));
            String token = getModMdoTokenFormat(address, TokenContentType.TOKEN_BY_ENCRYPTION);
            String loginType = getModMdoTokenFormat(address, TokenContentType.LOGIN_TYPE);
            client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(tokenChannel, (new PacketByteBuf(Unpooled.buffer())).writeString(client.player.getUuid().toString()).writeString(client.player.getName().asString()).writeString(loginType).writeString(token)));
        }
    }
}