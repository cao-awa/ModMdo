package com.github.cao.awa.modmdo.listeners;

import com.github.cao.awa.modmdo.network.forwarder.process.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ServerTickListener {

    /**
     * 添加服务器监听, 每tick结束以后执行一些需要的操作
     *
     * @author 草二号机
     */
    public void listener() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            PlayerManager players = server.getPlayerManager();

            EntrustExecution.tryTemporary(() -> eachPlayer(players));

            for (ModMdoDataProcessor processor : modmdoConnections) {
                processor.tick(server);
            }
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            event.submitGameTickStart(server);
        });
    }

    /**
     * 遍历每一位玩家执行操作
     *
     * @param players
     *         玩家管理器
     * @author 草awa
     * @author 草二号机
     */
    public void eachPlayer(PlayerManager players) {
        for (ServerPlayerEntity player : players.getPlayerList()) {
            if (modmdoWhitelist) {
                if (!hasWhitelist(player)) {
                    player.networkHandler.connection.send(new DisconnectS2CPacket(new TranslatableText("multiplayer.disconnect.not_whitelisted")));
                    player.networkHandler.connection.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted"));
                }
            }
        }
    }

    public boolean hasWhitelist(ServerPlayerEntity player) {
        try {
            switch (whitelist.get(player.getName().asString()).getRecorde().type()) {
                case IDENTIFIER -> {
                    if (whitelist.get(player.getName().asString()).getRecorde().modmdoUniqueId().equals("")) {
                        return false;
                    }
                }
                case UUID -> {
                    if (! player.getUuid().equals(whitelist.get(player.getName().asString()).getRecorde().uuid())) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

