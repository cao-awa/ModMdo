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

        String data1 = "";
        try {
            data1 = packetByteBuf.readString();
        } catch (Exception e) {
        }

        String data2 = "";
        try {
            data2 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        String data3 = "";
        try {
            data3 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        if(channel.equals(tokenChannel)) {
            if(! data1.equals("")) {
                if(data3.equals(modMdoServerToken)) {
                    LOGGER.info("login player: " + data1);

                    loginUsers.put(data1, new User(data2, data1).toJSONObject());
                    cacheUsers.removeUser(loginUsers.getUser(data1));
                }
            }
        }

        if(channel.equals(connectingChannel)) {
            if(!data1.equals("") & !data2.equals("")) {
                cacheUsers.put(data1,new User(data2,data1).toJSONObject());
            }
        }

        ci.cancel();
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        LOGGER.info("logout player: " + player.getUuid().toString());
        LOGGER.info("canceling player token for: " + player.getUuid().toString());
        try {
            loginUsers.removeUser(player);
            cacheUsers.removeUser(player);
        } catch (Exception e) {

        }
    }
}

