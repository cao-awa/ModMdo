package com.github.cao.awa.modmdo.event.client;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;

@Auto
public class ClientSettingEvent extends EntityTargetedEvent<ClientSettingEvent> {
    private final ServerPlayerEntity player;
    private final ClientSettingsC2SPacket packet;
    private final MinecraftServer server;

    public ClientSettingEvent(ServerPlayerEntity player, ClientSettingsC2SPacket packet, MinecraftServer server) {
        this.player = player;
        this.packet = packet;
        this.server = server;
    }

    private ClientSettingEvent() {
        this.player = null;
        this.packet = null;
        this.server = null;
    }

    @Override
    public String getName() {
        return "ClientSetting";
    }

    public static ClientSettingEvent snap() {
        return new ClientSettingEvent();
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public ObjectArrayList<LivingEntity> getTargeted() {
        ObjectArrayList<LivingEntity> list = new ObjectArrayList<>();
        list.add(player);
        return list;
    }

    public ClientSettingsC2SPacket getPacket() {
        return packet;
    }

    public String getLanguage() {
        return packet.getLanguage();
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ClientSettingEvent fuse(Previously<ClientSettingEvent> previously, ClientSettingEvent delay) {
        return previously.target();
    }

    @Override
    public String abbreviate() {
        return "ClientSettingEvent";
    }

    public String clazz() {
        return getClass().getName();
    }
}