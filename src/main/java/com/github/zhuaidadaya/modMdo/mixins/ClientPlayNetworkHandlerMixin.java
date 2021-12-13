package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.MCH.utils.config.Config;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.util.Identifier;
import org.json.JSONObject;
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

    @Shadow @Final private ClientConnection connection;

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
            client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(new Identifier("modmdo:connecting"), (new PacketByteBuf(Unpooled.buffer())).writeString(client.player.getUuid().toString()).writeString(client.player.getName().asString())));
            Config<Object, Object> token = config.getConfig("token_by_encryption");
            System.out.println();
            String tokenString = "";
            String loginType = "default";
            try {
                JSONObject format = new JSONObject(token.getValue()).getJSONObject("client").getJSONObject(connection.getAddress().toString());
                tokenString = format.get("token").toString();
                loginType = format.get("login_type").toString();
            } catch (Exception e) {

            }
            client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(tokenChannel, (new PacketByteBuf(Unpooled.buffer())).writeString(client.player.getUuid().toString()).writeString(client.player.getName().asString()).writeString(loginType).writeString(tokenString)));
        }
    }
}