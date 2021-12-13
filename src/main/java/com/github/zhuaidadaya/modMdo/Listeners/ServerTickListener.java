package com.github.zhuaidadaya.modMdo.listeners;

import com.github.zhuaidadaya.modMdo.commands.DimensionTips;
import com.github.zhuaidadaya.modMdo.commands.XYZ;
import com.github.zhuaidadaya.modMdo.type.ModMdoType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.LinkedHashMap;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

public class ServerTickListener {
    private final LinkedHashMap<ServerPlayerEntity, Long> skipMap = new LinkedHashMap<>();

    public void listener() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            PlayerManager players = server.getPlayerManager();

            if(enableDeadMessage) {
                detectPlayerDead(players);
            }

            if(modMdoType == ModMdoType.SERVER)
                checkLoginStat(players);

        });
    }

    public void checkLoginStat(PlayerManager players) {
        try {
            for(ServerPlayerEntity player : players.getPlayerList()) {
//                if(! loginUsers.hasUser(player) & ! cacheUsers.hasUser(player)) {
//                    player.networkHandler.disconnect(Text.of("unable to cache login stat"));
//                } else
                {
                    if(skipMap.get(player) == null)
                        skipMap.put(player, System.currentTimeMillis());

                    if(System.currentTimeMillis() - skipMap.get(player) > 600000) {
                        skipMap.put(player, System.currentTimeMillis());
                        try {
                            loginUsers.getUser(player.getUuid());
                            cacheUsers.removeUser(player);
                        } catch (Exception e) {
                            player.networkHandler.disconnect(Text.of("invalid token, check your login stat"));
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    public void detectPlayerDead(PlayerManager players) {
        try {
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
