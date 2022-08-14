package com.github.cao.awa.modmdo.mixins.connection;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;
import net.minecraft.network.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow @Nullable public abstract Text getDisconnectReason();

    @Shadow
    private Channel channel;
    @Shadow
    private boolean errored;

    @Shadow
    public abstract void send(Packet<?> packet);

    @Inject(method = "disconnect", at = @At("RETURN"))
    public void disconnect(CallbackInfo ci) {
        TRACKER.submit("Disconnect: " + EntrustParser.tryCreate(() -> Objects.requireNonNull(getDisconnectReason()).asString(), null));
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

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V", at = @At("HEAD"))
    public void send(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci) {
        TRACKER.submit("Send packet: " + packet.getClass());
    }
}
