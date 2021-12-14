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

    /**
     * 添加服务器监听, 每tick结束以后执行一些需要的操作
     *
     * @author 草二号机
     */
    public void listener() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            PlayerManager players = server.getPlayerManager();

            try {
                eachPlayer(players);
            } catch (Exception e) {

            }
        });
    }

    /**
     * 遍历每一位玩家执行操作
     *
     * @author 草awa
     * @author 草二号机
     *
     * @param players 玩家管理器
     */
    public void eachPlayer(PlayerManager players) {
        for(ServerPlayerEntity player : players.getPlayerList()) {
            if(enableDeadMessage)
                detectPlayerDead(player);
            if(modMdoType == ModMdoType.SERVER & enableEncryptionToken)
                checkLoginStat(player);
            setPlayerLevel(player, players);
        }
    }

    /**
     * 设置玩家的权限等级, 处理使用不同的token登录时获得的不同权限等级
     *
     * @author 草二号机
     * @author 草awa
     *
     * @param player 玩家
     * @param manager 玩家管理器
     */
    public void setPlayerLevel(ServerPlayerEntity player, PlayerManager manager) {
        try {
            int level = loginUsers.getUserLevel(player);

            if(manager.isOperator(player.getGameProfile())) {
                if(level == 1)
                    manager.removeFromOperators(player.getGameProfile());
            } else if(level == 4) {
                manager.addToOperators(player.getGameProfile());
            }
        } catch (Exception e) {

        }
    }

    /**
     * 检查玩家的登入状态, 如果超过指定时间没有登入则断开连接并提示检查token
     *
     * @author zhuaidadaya
     * @author 草awa
     * @author 草二号机
     *
     * @param player 玩家
     */
    public void checkLoginStat(ServerPlayerEntity player) {
        try {
            if(! loginUsers.hasUser(player)) {
                if(skipMap.get(player) == null)
                    skipMap.put(player, System.currentTimeMillis());

                if(System.currentTimeMillis() - skipMap.get(player) > 650) {
                    skipMap.put(player, System.currentTimeMillis());
                    try {
                        loginUsers.getUser(player.getUuid());
                    } catch (Exception e) {
                        player.networkHandler.disconnect(Text.of("invalid token, check your login stat"));
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 检测玩家的死亡状态, 如果死亡时间为1则发送当时的坐标和维度信息
     * (如果该玩家愿意接收才发送)
     *
     * @author 草二号机
     *
     * @param player 玩家
     */
    public void detectPlayerDead(ServerPlayerEntity player) {
        try {
            if(isUserDeadMessageReceive(player.getUuid()) & enableDeadMessage) {
                if(player.deathTime == 1) {
                    DimensionTips dimensionTips = new DimensionTips();
                    XYZ xyz = new XYZ(player.getX(), player.getY(), player.getZ());
                    player.sendMessage(formatDeathMessage(player, dimensionTips, xyz), false);
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 对死亡时的位置、维度进行格式化
     *
     * @author 草二号机
     *
     * @param player 玩家
     * @param dimensionTips 用于格式化的DimensionTips
     * @param xyz 等同于vec3d
     * @return 格式化过后的信息
     */
    public TranslatableText formatDeathMessage(ServerPlayerEntity player, DimensionTips dimensionTips, XYZ xyz) {
        String dimension = dimensionTips.getDimension(player);
        return new TranslatableText("dead.deadIn", dimensionTips.getDimensionColor(dimension), dimensionTips.getDimensionName(dimension), xyz.getIntegerXYZ());
    }
}
