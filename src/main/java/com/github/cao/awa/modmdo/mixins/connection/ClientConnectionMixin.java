package com.github.cao.awa.modmdo.mixins.connection;

import io.netty.channel.*;
import io.netty.util.concurrent.*;
import net.minecraft.network.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow
    private Channel channel;
    @Shadow
    private boolean errored;

    @Shadow
    public abstract void send(Packet<?> packet);

    @Inject(method = "disconnect", at = @At("HEAD"))
    public void disconnect(CallbackInfo ci) {
        TRACKER.submit("Disconnect");
    }

    /**
     * @author 草awa
     */
    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    public void exceptionCaught(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        ex.printStackTrace();
    }

    @Shadow
    protected abstract NetworkState getState();

    @Shadow
    public abstract void disconnect(Text disconnectReason);

    @Shadow
    public abstract void disableAutoRead();

    @Shadow
    public abstract void send(Packet<?> packet, @Nullable GenericFutureListener<? extends Future<? super Void>> callback);
}
