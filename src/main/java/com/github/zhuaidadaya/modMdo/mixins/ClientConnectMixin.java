package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;

@Mixin(ClientConnection.class)
public class ClientConnectMixin {
    @Inject(method = "connect",at = @At("RETURN"))
    private static void connect(InetSocketAddress address, boolean useEpoll, CallbackInfoReturnable<ClientConnection> cir) {
//        System.out.println("connect??");

//        client.player.networkHandler.sendPacket(new CustomPayloadC2SPacket(tokenChannel, (new PacketByteBuf(Unpooled.buffer())).writeString("test")));
    }
}
