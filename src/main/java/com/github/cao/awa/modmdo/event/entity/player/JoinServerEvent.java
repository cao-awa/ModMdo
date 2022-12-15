package com.github.cao.awa.modmdo.event.entity.player;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.util.math.*;

@Auto
public class JoinServerEvent extends EntityTargetedEvent<JoinServerEvent> {
    private final LivingEntity player;
    private final ClientConnection connection;
    private final Vec3d pos;
    private final MinecraftServer server;

    public JoinServerEvent(ServerPlayerEntity player, ClientConnection connection, Vec3d pos, MinecraftServer server) {
        this.player = player;
        this.pos = pos;
        this.server = server;
        this.connection = connection;
    }

    private JoinServerEvent() {
        this.player = null;
        this.pos = null;
        this.server = null;
        this.connection = null;
    }

    @Override
    public String getName() {
        return "JoinServer";
    }

    public static JoinServerEvent snap() {
        return new JoinServerEvent();
    }

    public LivingEntity getPlayer() {
        return player;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public ObjectArrayList<LivingEntity> getTargeted() {
        ObjectArrayList<LivingEntity> list = new ObjectArrayList<>();
        list.add(player);
        return list;
    }

    public Vec3d getPos() {
        return pos;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public JoinServerEvent fuse(Previously<JoinServerEvent> previously, JoinServerEvent delay) {
        return previously.target();
    }

    @Override
    public String abbreviate() {
        return "JoinServerEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}
