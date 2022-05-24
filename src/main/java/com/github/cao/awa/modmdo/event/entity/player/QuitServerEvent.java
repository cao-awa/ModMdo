package com.github.cao.awa.modmdo.event.entity.player;

import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.util.math.*;

public class QuitServerEvent extends EntityTargetedEvent<QuitServerEvent> {
    private final LivingEntity player;
    private final ClientConnection connection;
    private final Vec3d pos;
    private final MinecraftServer server;

    public QuitServerEvent(ServerPlayerEntity player, ClientConnection connection, Vec3d pos, MinecraftServer server) {
        this.player = player;
        this.pos = pos;
        this.server = server;
        this.connection = connection;
    }

    private QuitServerEvent() {
        this.player = null;
        this.pos = null;
        this.server = null;
        this.connection = null;
    }

    public static QuitServerEvent snap() {
        return new QuitServerEvent();
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

    public QuitServerEvent fuse(Previously<QuitServerEvent> previously, QuitServerEvent delay) {
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
        return EntrustParser.tryCreate(() -> String.format("QuitServerEvent{player=%s, pos=%s, dimension=%s}", name, pos, player.getEntityWorld().getDimension().getEffects()), toString());
    }

    @Override
    public String abbreviate() {
        return "QuitServerEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}
