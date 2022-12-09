package com.github.cao.awa.modmdo.event.server.chat;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;

@Auto
public class GameChatEvent extends EntityTargetedEvent<GameChatEvent> {
    private final LivingEntity player;
    private final ChatMessageC2SPacket packet;
    private final MinecraftServer server;

    public GameChatEvent(ServerPlayerEntity player, ChatMessageC2SPacket packet, MinecraftServer server) {
        this.player = player;
        this.packet = packet;
        this.server = server;
    }

    private GameChatEvent() {
        this.player = null;
        this.packet = null;
        this.server = null;
    }

    public static GameChatEvent snap() {
        return new GameChatEvent();
    }

    public LivingEntity getPlayer() {
        return player;
    }

    public ObjectArrayList<LivingEntity> getTargeted() {
        ObjectArrayList<LivingEntity> list = new ObjectArrayList<>();
        list.add(player);
        return list;
    }

    public ChatMessageC2SPacket getPacket() {
        return packet;
    }

    public String getMessage() {
        return packet.getChatMessage();
    }

    public MinecraftServer getServer() {
        return server;
    }

    public GameChatEvent fuse(Previously<GameChatEvent> previously, GameChatEvent delay) {
        return previously.target();
    }

    @Override
    public String abbreviate() {
        return "GameChatEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}
