package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.modmdo.storage.Variables;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.SocketAddress;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

    @Shadow
    private Channel channel;
    @Shadow
    private SocketAddress address;

    /**
     * @author 草awa
     */
    @Inject(method = "handleDisconnection", cancellable = true, at = @At("HEAD"))
    public void handleDisconnection(CallbackInfo ci) {
        if(this.channel == null || this.channel.isOpen()) {
            ci.cancel();
        }
        Variables.disconnectedSet.add(address);
    }
}
