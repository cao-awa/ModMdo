package com.github.zhuaidadaya.modMdo.listeners;

import com.github.zhuaidadaya.modMdo.commands.DimensionTips;
import com.github.zhuaidadaya.modMdo.commands.XYZ;
import com.github.zhuaidadaya.modMdo.type.ModMdoType;
import com.github.zhuaidadaya.modMdo.usr.User;
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
                e.printStackTrace();
            }
        });
    }

    /**
     * 遍历每一位玩家执行操作
     *
     * @param players
     *         玩家管理器
     *
     * @author 草awa
     * @author 草二号机
     */
    public void eachPlayer(PlayerManager players) {
        for(ServerPlayerEntity player : players.getPlayerList()) {
            if(modMdoType == ModMdoType.SERVER & enableEncryptionToken) {
                checkLoginStat(player);
                cancelLoginIfNoExistentOrChangedToken(player, players);
            }
            if(enableDeadMessage)
                detectPlayerDead(player);
            setPlayerLevel(player, players);
        }
    }

    /**
     * 当玩家不存在时, 清除登入信息
     * (多个位置都有尝试清除, 保证一定能够移除登入状态)
     * <p>
     * 或者服务器Token改变时, 也清除登入信息
     * (当token不符合时移除玩家, 换用新token即可)
     * 这种情况一般在手动生成新的token时使用, 否则一般不会
     *
     * @param player
     *         玩家
     * @param manager
     *         玩家管理器
     *
     * @author 草awa
     */
    public void cancelLoginIfNoExistentOrChangedToken(ServerPlayerEntity player, PlayerManager manager) {
        try {
            for(User user : loginUsers.getUsers()) {
                if(manager.getPlayer(user.getName()) == null) {
                    loginUsers.removeUser(user);
                }
            }

            if(manager.getPlayerList().contains(player)) {
                User user = loginUsers.getUser(player);
                if(user.getLevel() == 1) {
                    if(! user.getClientToken().getToken().equals(modMdoToken.getServerToken().getServerDefaultToken())) {
                        loginUsers.removeUser(player);
                    }
                } else if(user.getLevel() == 4) {
                    if(! user.getClientToken().getToken().equals(modMdoToken.getServerToken().getServerOpsToken())) {
                        loginUsers.removeUser(player);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 设置玩家的权限等级, 处理使用不同的token登录时获得的不同权限等级
     *
     * @param player
     *         玩家
     * @param manager
     *         玩家管理器
     *
     * @author 草二号机
     * @author 草awa
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
     * @param player
     *         玩家
     *
     * @author zhuaidadaya
     * @author 草awa
     * @author 草二号机
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
     * @param player
     *         玩家
     *
     * @author 草二号机
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
     * @param player
     *         玩家
     * @param dimensionTips
     *         用于格式化的DimensionTips
     * @param xyz
     *         等同于vec3d
     *
     * @return 格式化过后的信息
     *
     * @author 草二号机
     */
    public TranslatableText formatDeathMessage(ServerPlayerEntity player, DimensionTips dimensionTips, XYZ xyz) {
        String dimension = dimensionTips.getDimension(player);
        return new TranslatableText("dead.deadIn", dimensionTips.getDimensionColor(dimension), dimensionTips.getDimensionName(dimension), xyz.getIntegerXYZ());
    }
}
