package com.github.cao.awa.modmdo.ranking;

import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.stat.*;
import net.minecraft.util.*;

public abstract class Rank extends Storable {
    private static final Object2ObjectOpenHashMap<String, Object2ObjectOpenHashMap<String, Rank>> suggestion = new Object2ObjectOpenHashMap<>();
    public final MinecraftServer server;

    public Rank(MinecraftServer server, ServerPlayerEntity player) {
        this.server = server;
    }

    public abstract Object2IntArrayMap<ServerPlayerEntity> stat();

    public Object2IntArrayMap<ServerPlayerEntity> parse(String target) {
        Object2IntArrayMap<ServerPlayerEntity> map = new Object2IntArrayMap<>();
        EntrustExecution.tryFor(server.getPlayerManager().getPlayerList(), player -> {
            map.put(player, player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(new Identifier(target))));
        });
        return map;
    }

    public abstract String name();

    public void suggestion() {

    }

    public Object2ObjectOpenHashMap<String, Rank> get(String name) {
        return suggestion.get(name);
    }

    public abstract void update();
}
