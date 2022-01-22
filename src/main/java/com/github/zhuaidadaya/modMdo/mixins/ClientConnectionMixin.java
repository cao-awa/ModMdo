package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.modMdo.storage.Variables;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.net.SocketAddress;

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private Channel channel;
    @Shadow
    private boolean disconnected;

    @Shadow
    @Nullable
    public abstract Text getDisconnectReason();

    @Shadow
    public abstract PacketListener getPacketListener();

    @Shadow private SocketAddress address;

    @Shadow public abstract boolean isOpen();

    /**
     * @author 草awa
     *
     * @reason
     */
    @Overwrite
    public void handleDisconnection() {
        if(this.channel == null || this.channel.isOpen()) {
            return;
        }
        if(!disconnected) {
            this.disconnected = true;
            if(this.getDisconnectReason() != null) {
                this.getPacketListener().onDisconnected(this.getDisconnectReason());
            } else if(this.getPacketListener() != null) {
                this.getPacketListener().onDisconnected(new TranslatableText("multiplayer.disconnect.generic"));
            }
            Variables.disconnectedSet.add(address);
        }
    }
}
