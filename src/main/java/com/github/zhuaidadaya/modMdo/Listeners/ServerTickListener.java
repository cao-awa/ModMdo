package com.github.zhuaidadaya.modMdo.listeners;

import com.github.zhuaidadaya.modMdo.commands.DimensionTips;
import com.github.zhuaidadaya.modMdo.commands.XYZ;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

public class ServerTickListener {
    public void listener() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if(enableDeadMessage) {
                detectPlayerDead();
            }
        });
    }

    public void detectPlayerDead() {
        try {
            PlayerManager players = server.getPlayerManager();

            for(ServerPlayerEntity player : players.getPlayerList()) {
                if(isUserDeadMessageReceive(player.getUuid())) {
                    if(player.deathTime == 1) {
                        DimensionTips dimensionTips = new DimensionTips();
                        XYZ xyz = new XYZ(player.getX(), player.getY(), player.getZ());
                        player.sendMessage(formatDeathMessage(player, dimensionTips, xyz), false);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public TranslatableText formatDeathMessage(ServerPlayerEntity player, DimensionTips dimensionTips, XYZ xyz) {
        String dimension = dimensionTips.getDimension(player);
        return new TranslatableText("dead.deadIn", dimensionTips.getDimensionColor(dimension), dimensionTips.getDimensionName(dimension), xyz.getIntegerXYZ());
    }
}
