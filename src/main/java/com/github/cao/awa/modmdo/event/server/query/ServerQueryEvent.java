package com.github.cao.awa.modmdo.event.server.query;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.delay.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.s2c.query.*;
import net.minecraft.server.*;

@Auto
public class ServerQueryEvent extends ModMdoEvent<ServerQueryEvent> {
    private final ClientConnection connection;
    private final QueryResponseS2CPacket packet;
    private final MinecraftServer server;

    public ServerQueryEvent(ClientConnection connection, QueryResponseS2CPacket packet, MinecraftServer server) {
        this.connection = connection;
        this.server = server;
        this.packet = packet;
    }

    private ServerQueryEvent() {
        this.connection = null;
        this.packet = null;
        this.server = null;
    }

    @Override
    public String getName() {
        return "ServerQuery";
    }

    public static ServerQueryEvent snap() {
        return new ServerQueryEvent();
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public QueryResponseS2CPacket getPacket() {
        return packet;
    }

    public ServerQueryEvent fuse(Previously<ServerQueryEvent> previously, ServerQueryEvent delay) {
        return previously.target();
    }

    @Override
    public String abbreviate() {
        return "ServerQueryEvent";
    }

    public String clazz() {
        return getClass().getName();
    }

    @Override
    public void adaptive(ServerQueryEvent event) {
        refrainAsync(event);
    }
}
