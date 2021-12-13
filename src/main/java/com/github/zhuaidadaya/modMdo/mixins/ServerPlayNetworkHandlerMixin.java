package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.modMdo.usr.User;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        Identifier channel = new Identifier("");
        try {
            channel = packet.getChannel();
        } catch (Exception e) {
        }

        PacketByteBuf packetByteBuf = null;
        try {
            packetByteBuf = packet.getData();
        } catch (Exception e) {
        }

        String uuid = "";
        try {
            uuid = packetByteBuf.readString();
        } catch (Exception e) {
        }

        String name = "";
        try {
            name = packetByteBuf.readString();
        } catch (Exception e) {

        }

        String token = "";
        try {
            token = packetByteBuf.readString();
        } catch (Exception e) {

        }

        if(channel.equals(tokenChannel)) {
            if(! uuid.equals("")) {
                if(token.equals(modMdoServerToken)) {
                    LOGGER.info("login player: " + uuid);

                    loginUsers.put(uuid, new User(name, uuid).toJSONObject());
                }
            }
        }

        ci.cancel();
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        LOGGER.info("logout player: " + player.getUuid().toString());
        LOGGER.info("canceling player token for: " + player.getUuid().toString());
        loginUsers.removeUser(player);
    }
}

