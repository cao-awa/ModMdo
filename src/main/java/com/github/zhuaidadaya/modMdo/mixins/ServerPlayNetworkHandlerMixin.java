package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            System.out.println("er1");
        }

        PacketByteBuf packetByteBuf = null;
        try {
            packetByteBuf = packet.getData();
        } catch (Exception e) {
            System.out.println("er2");
        }

        System.out.println(channel);
        try {
            String cache = packetByteBuf.readString();
            System.out.println(cache);
        } catch (Exception e) {
            System.out.println("er3");
        }

        ci.cancel();
    }
}

