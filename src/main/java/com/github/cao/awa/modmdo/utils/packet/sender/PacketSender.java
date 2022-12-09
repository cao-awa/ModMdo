package com.github.cao.awa.modmdo.utils.packet.sender;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.stack.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

public abstract class PacketSender<T extends PacketSender<T>> {
    private final Stacker<Packet<?>> packets = new Stacker<>();
    private @NotNull ClientConnection connection;
    private ClientConnection swapConnection;
    private @NotNull Identifier channel;

    public PacketSender(@NotNull ClientConnection connection, @NotNull Identifier channel) {
        this.connection = connection;
        this.channel = channel;
    }

    public @NotNull ClientConnection getConnection() {
        return connection;
    }

    public void setConnection(@NotNull ClientConnection connection) {
        this.connection = connection;
    }

    public ClientConnection getSwapConnection() {
        return swapConnection;
    }

    public void setSwapConnection(ClientConnection swapConnection) {
        this.swapConnection = swapConnection;
    }

    public @NotNull Identifier getChannel() {
        return channel;
    }

    public void setChannel(@NotNull Identifier channel) {
        this.channel = channel;
    }

    public void prepare(Packet<?> packet) {
        this.packets.push(packet);
    }

    public abstract T swap(@NotNull ClientConnection connection);

    public void send() {
        if (this.getPackets()
                .size() == 0) {
            return;
        }
        this.getPackets()
            .popEach(this::send);
        if (this.swapConnection != null) {
            this.connection = this.swapConnection;
            this.swapConnection = null;
        }
    }

    public Stacker<Packet<?>> getPackets() {
        return packets;
    }

    private void send(Packet<?> packet) {
        this.connection.send(packet);
    }
}
