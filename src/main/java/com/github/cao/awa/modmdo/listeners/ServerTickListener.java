package com.github.cao.awa.modmdo.listeners;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.event.server.tick.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.network.forwarder.process.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;

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
            event.submit(new GameTickStartEvent(server));
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
                    player.networkHandler.connection.send(new DisconnectS2CPacket(TextUtil.translatable("multiplayer.disconnect.not_whitelisted").text()));
                    player.networkHandler.connection.disconnect(TextUtil.translatable("multiplayer.disconnect.not_whitelisted").text());
                }
                if (hasBan(player)) {
                    Certificate ban = banned.get(player.getName().asString());
                    if (ban instanceof TemporaryCertificate temporary) {
                        String remaining = temporary.formatRemaining();
                        player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining).text()));
                        player.networkHandler.connection.disconnect(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining).text());
                    } else {
                        player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite").text()));
                        player.networkHandler.connection.disconnect(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite").text());
                    }
                }
            }
        }
    }
}
