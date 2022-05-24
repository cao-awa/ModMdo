package com.github.zhuaidadaya.modmdo.event.server.tick;

import com.github.zhuaidadaya.modmdo.event.delay.*;
import com.github.zhuaidadaya.modmdo.event.entity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.server.*;

public class GameTickStartEvent extends EntityTargetedEvent<GameTickStartEvent> {
    private final MinecraftServer server;

    public GameTickStartEvent(MinecraftServer server) {
        this.server = server;
    }

    private GameTickStartEvent() {
        this.server = null;
    }

    public static GameTickStartEvent snap() {
        return new GameTickStartEvent();
    }

    public MinecraftServer getServer() {
        return server;
    }

    public GameTickStartEvent fuse(Previously<GameTickStartEvent> previously, GameTickStartEvent delay) {
        return previously.target();
    }

    public String synopsis() {
        return EntrustParser.tryCreate(() -> "GameTickStartEvent{}", toString());
    }

    @Override
    public String abbreviate() {
        return "GameTickStartEvent";
    }

    public String clazz() {
        return getClass().getName();
    }

    @Override
    public ObjectArrayList<? extends Entity> getTargeted() {
        ObjectArrayList<Entity> targeted = new ObjectArrayList<>();
        EntrustExecution.tryTemporary(() -> targeted.addAll(server.getPlayerManager().getPlayerList()));
        return targeted;
    }
}
