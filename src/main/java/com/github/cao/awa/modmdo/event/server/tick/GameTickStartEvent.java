package com.github.cao.awa.modmdo.event.server.tick;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.event.delay.*;
import com.github.cao.awa.modmdo.event.entity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;
import net.minecraft.server.*;

@Auto
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

    @Override
    public String getName() {
        return "GameTickStart";
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
        return EntrustEnvironment.operation(new ObjectArrayList<>(), targeted -> EntrustEnvironment.trys(() -> targeted.addAll(server.getPlayerManager().getPlayerList())));
    }

    @Override
    public void adaptive(GameTickStartEvent event) {
        refrainAsync(event);
    }
}
