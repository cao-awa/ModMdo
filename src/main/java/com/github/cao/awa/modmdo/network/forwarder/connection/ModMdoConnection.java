package com.github.cao.awa.modmdo.network.forwarder.connection;

import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;

public class ModMdoConnection {
    private final ClientConnection connection;
    private int sent = 0;

    public ModMdoConnection(ClientConnection connection) {
        this.connection = connection;
    }

    public void send(CustomPayloadC2SPacket packet) {
        sent += packet.getData().readableBytes();
        connection.send(packet);
    }

    public int getSent() {
        return sent;
    }

    public void send(CustomPayloadS2CPacket packet) {
        sent += packet.getData().readableBytes();
        connection.send(packet);
    }
}
