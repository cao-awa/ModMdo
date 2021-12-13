package com.github.zhuaidadaya.modMdo.mixins;

import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
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

import static com.github.zhuaidadaya.modMdo.storage.Variables.tokenChannel;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Final
    @Shadow
    private MinecraftClient client;

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onOnCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        System.out.println("check token");

        PacketByteBuf data = packet.getData();

        try {
            if(data != null) {
                int id = data.readVarInt();
                if(id == 99)
                    client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(tokenChannel, (new PacketByteBuf(Unpooled.buffer())).writeString("test")));
                if(id == 96)
                    System.out.println("96");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ci.cancel();
    }

    @Inject(method = "onGameJoin",at = @At("RETURN"))
    public void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        client.getNetworkHandler().sendPacket(new CustomPayloadC2SPacket(tokenChannel, (new PacketByteBuf(Unpooled.buffer())).writeString("test")));
    }
}