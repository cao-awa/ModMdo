package com.github.cao.awa.modmdo.event.server;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.delay.*;
import net.minecraft.server.*;

@Auto
public class ServerStartedEvent extends ModMdoEvent<ServerStartedEvent> {
    private final MinecraftServer server;

    public ServerStartedEvent(MinecraftServer server) {
        this.server = server;
    }

    private ServerStartedEvent() {
        this.server = null;
    }

    public static ServerStartedEvent snap() {
        return new ServerStartedEvent();
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ServerStartedEvent fuse(Previously<ServerStartedEvent> previously, ServerStartedEvent delay) {
        return previously.target();
    }

    @Override
    public String getName() {
        return "ServerStarted";
    }

    @Override
    public String abbreviate() {
        return "ServerStartedEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}
