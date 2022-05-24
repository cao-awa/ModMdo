package com.github.zhuaidadaya.modmdo.event.entity.player;

import com.github.zhuaidadaya.modmdo.event.delay.*;
import com.github.zhuaidadaya.modmdo.event.entity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.util.math.*;

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
        return ObjectArrayList.of(player);
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

    public String synopsis() {
        String name = EntrustParser.trying(() -> EntrustParser.tryCreate(() -> {
            String str = player.getDisplayName().asString();
            if (str.equals("")) {
                throw new IllegalArgumentException("empty name");
            }
            return str;
        }, player.toString()), () -> "null");
        return EntrustParser.tryCreate(() -> String.format("JoinServerEvent{player=%s, pos=%s, dimension=%s}", name, pos, player.getEntityWorld().getDimension().getEffects()), toString());
    }

    @Override
    public String abbreviate() {
        return "JoinServerEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}
