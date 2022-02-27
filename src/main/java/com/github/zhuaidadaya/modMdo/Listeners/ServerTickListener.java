package com.github.zhuaidadaya.modMdo.listeners;

import com.github.zhuaidadaya.modMdo.simple.vec.XYZ;
import com.github.zhuaidadaya.modMdo.storage.Variables;
import com.github.zhuaidadaya.modMdo.type.ModMdoType;
import com.github.zhuaidadaya.modMdo.usr.User;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

public class ServerTickListener {
    private long lastAddOnlineTime = - 1;
    private long lastIntervalActive = System.currentTimeMillis();
    private int randomRankingSwitchTick = 0;

    /**
     * 添加服务器监听, 每tick结束以后执行一些需要的操作
     *
     * @author 草二号机
     */
    public void listener() {
        lastAddOnlineTime = System.currentTimeMillis();

        Thread subListener = new Thread(() -> {
            while(server == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
            while(server.isRunning()) {
                try {
                    PlayerManager players = server.getPlayerManager();

                    setOnlineTimeAndRanking(server, players);

                    updateRankingShow(server);

                    if(System.currentTimeMillis() - lastIntervalActive > 1000) {
                        updateUserProfiles();
                        if(rankingIsStatObject(rankingObject)) {
                            updateOtherRankings(server, players);
                        }
                        lastIntervalActive = System.currentTimeMillis();
                    }

                    if(rankingRandomSwitchInterval != - 1) {
                        if(randomRankingSwitchTick > rankingRandomSwitchInterval) {
                            if(rankingSwitchNoDump)
                                config.set("ranking_object", rankingObject = getRandomRankingObjectNoDump());
                            else
                                config.set("ranking_object", rankingObject = getRandomRankingObject());
                            randomRankingSwitchTick = 0;
                        }
                    }

                    Thread.sleep(20);
                } catch (Exception e) {

                }
            }
        });

        subListener.setName("ModMdo sub listener thread");

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            Thread listener = new Thread(() -> {
                PlayerManager players = server.getPlayerManager();

                randomRankingSwitchTick++;

                Variables.server = server;

                try {
                    eachPlayer(players);
                } catch (Exception e) {

                }
            });

            listener.setName("ModMdo listener thread");

            listener.start();
        });

        subListener.start();
    }

    public void updateCustomRanking(String object, MinecraftServer server, ServerPlayerEntity player, JSONObject stat) {
        try {
            JSONObject custom = stat.getJSONObject("minecraft:custom");

            String scoreboardObject = "";
            String countObject = "";

            switch(object) {
                case "minecraft:traded_with_villager" -> {
                    scoreboardObject = "modmdo.trd";
                    countObject = "minecraft:traded_with_villager";
                }
                case "minecraft:deaths" -> {
                    scoreboardObject = "modmdo.dts";
                    countObject = "minecraft:deaths";
                }
            }
            int count = custom.getInt(countObject);

            ServerScoreboard scoreboard = server.getScoreboard();
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(player.getName().asString(), scoreboard.getObjective(scoreboardObject));

            scoreboardPlayerScore.setScore(count);
            scoreboard.updateScore(scoreboardPlayerScore);
        } catch (Exception e) {

        }
    }

    public void updateOtherRankings(MinecraftServer server, PlayerManager manager) {
        for(ServerPlayerEntity player : manager.getPlayerList()) {
            User user = users.getUser(player);
            if(! user.isDummyPlayer()) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(getServerLevelPath(server) + "stats/" + player.getUuid().toString() + ".json"));

                    player.getStatHandler().save();

                    String cache;
                    StringBuilder builder = new StringBuilder();
                    while((cache = reader.readLine()) != null) {
                        builder.append(cache);
                    }

                    JSONObject source = new JSONObject(builder.toString());
                    JSONObject stat = source.getJSONObject("stats");

                    switch(rankingObject) {
                        case "destroy.blocks" -> {
                            updateDestroyBlocks(server, player, stat);
                        }
                        case "villager.trades" -> {
                            updateCustomRanking("minecraft:traded_with_villager", server, player, stat);
                        }
                        case "player.deaths" -> {
                            updateCustomRanking("minecraft:deaths", server, player, stat);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    public void updateDestroyBlocks(MinecraftServer server, ServerPlayerEntity player, JSONObject stat) {
        try {
            JSONObject mined = stat.getJSONObject("minecraft:mined");

            int minedCount = 0;

            for(String s : mined.keySet()) {
                minedCount += mined.getInt(s);
            }

            ServerScoreboard scoreboard = server.getScoreboard();
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(player.getName().asString(), scoreboard.getObjective("modmdo.dsy"));

            scoreboardPlayerScore.setScore(minedCount);
            scoreboard.updateScore(scoreboardPlayerScore);
        } catch (Exception e) {

        }
    }

    public void updateOnlineTime(MinecraftServer server, ServerPlayerEntity player) {
        User user = users.getUser(player);
        if(! user.isDummyPlayer()) {
            ServerScoreboard scoreboard = server.getScoreboard();
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(player.getName().asString(), scoreboard.getObjective("modmdo.ots"));
            long showOnlineTime;
            switch(rankingOnlineTimeScale) {
                case "second" -> {
                    showOnlineTime = user.getOnlineSecond();
                }
                case "hour" -> {
                    showOnlineTime = user.getOnlineHour();
                }
                case "day" -> {
                    showOnlineTime = user.getOnlineDay();
                }
                case "month" -> {
                    showOnlineTime = user.getOnlineMonth();
                }
                default -> {
                    showOnlineTime = user.getOnlineMinute();
                }
            }
            scoreboardPlayerScore.setScore((int) showOnlineTime);
            scoreboard.updateScore(scoreboardPlayerScore);
        }
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
                checkLoginStat(player, players);
                try {
                    cancelLoginIfNoExistentOrChangedToken(player, players);
                } catch (Exception e) {

                }
                setPlayerLevel(player, players);
            }
            if(enableDeadMessage)
                detectPlayerDead(player);
        }
    }

    public void setOnlineTimeAndRanking(MinecraftServer server, PlayerManager players) {
        long current = System.currentTimeMillis();
        for(ServerPlayerEntity player : players.getPlayerList()) {
            User userCache = loginUsers.getUser(player);
            User user = users.getUser(player);

            user.setClientToken(userCache.getClientToken());

            if(userCache.getOnlineTime() == 0) {
                userCache.setOnlineTime(user.getOnlineTime());
            }

            userCache.addOnlineTime(Math.max(0, current - lastAddOnlineTime));

            for(String s : user.getFollows())
                userCache.addFollows(s);

            loginUsers.put(userCache);
            users.put(userCache);

            if(enableRanking) {
                if(rankingObject.equals("online.times")) {
                    updateOnlineTime(server, player);
                }
            }
        }
        lastAddOnlineTime = System.currentTimeMillis();
    }

    public void updateRankingShow(MinecraftServer server) {
        ServerScoreboard scoreboard = server.getScoreboard();

        if(enableRanking) {
            if(scoreboard.containsObjective("modmdo.dsy")) {
                rankingObjects.add("destroy.blocks");
            }
            if(scoreboard.containsObjective("modmdo.ots")) {
                rankingObjects.add("online.times");
            }
            if(scoreboard.containsObjective("modmdo.trd")) {
                rankingObjects.add("villager.trades");
            }

            switch(rankingObject) {
                case "online.times" -> scoreboard.setObjectiveSlot(1, scoreboard.getObjective("modmdo.ots"));
                case "destroy.blocks" -> scoreboard.setObjectiveSlot(1, scoreboard.getObjective("modmdo.dsy"));
                case "villager.trades" -> scoreboard.setObjectiveSlot(1, scoreboard.getObjective("modmdo.trd"));
            }
        } else {
            if(scoreboard.containsObjective("modmdo.dsy"))
                scoreboard.removeObjective(scoreboard.getObjective("modmdo.dsy"));
            if(scoreboard.containsObjective("modmdo.ots"))
                scoreboard.removeObjective(scoreboard.getObjective("modmdo.ots"));
            if(scoreboard.containsObjective("modmdo.trd"))
                scoreboard.removeObjective(scoreboard.getObjective("modmdo.trd"));
        }
    }

    /**
     * 当玩家不存在时, 清除登入信息<br>
     * (多个位置都有尝试清除, 保证一定能够移除登入状态)<br>
     * <br>
     * 或者服务器Token改变时, 也清除登入信息<br>
     * (当token不符合时移除玩家, 换用新token即可)<br>
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
            if((tokenChanged || enableCheckTokenPerTick) & ! forceStopTokenCheck) {
                for(User user : loginUsers.getUsers()) {
                    if(manager.getPlayer(user.getUuid()) == null) {
                        if(forceStopTokenCheck)
                            break;
                        player.networkHandler.sendPacket(new DisconnectS2CPacket(new LiteralText("obsolete player")));
                        player.networkHandler.disconnect(new LiteralText("obsolete player"));
                    }
                }

                if(forceStopTokenCheck)
                    return;

                if(manager.getPlayerList().contains(player)) {
                    if(loginUsers.hasUser(player)) {
                        User user = loginUsers.getUser(player);
                        if(user.getClientToken().getToken().equals("")) {
                            player.networkHandler.sendPacket(new DisconnectS2CPacket(new LiteralText("empty token, please update")));
                            player.networkHandler.disconnect(new LiteralText("empty token, please update"));
                        } else {
                            if(user.getLevel() == 1) {
                                if(! user.getClientToken().getToken().equals(modMdoToken.getServerToken().getServerDefaultToken())) {
                                    loginUsers.removeUser(player);
                                    player.networkHandler.sendPacket(new DisconnectS2CPacket(new LiteralText("obsolete token, please update")));
                                    player.networkHandler.disconnect(new LiteralText("obsolete token, please update"));
                                }
                            } else if(user.getLevel() == 4) {
                                if(! user.getClientToken().getToken().equals(modMdoToken.getServerToken().getServerOpsToken())) {
                                    player.networkHandler.sendPacket(new DisconnectS2CPacket(new LiteralText("obsolete token, please update")));
                                    player.networkHandler.disconnect(new LiteralText("obsolete token, please update"));
                                }
                            }
                        }
                    }
                }

                tokenChanged = false;
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
    public void checkLoginStat(ServerPlayerEntity player, PlayerManager manager) {
        try {
            if(! loginUsers.hasUser(player)) {
                if(skipMap.get(player) == null)
                    skipMap.put(player, System.currentTimeMillis());

                if(System.currentTimeMillis() - skipMap.get(player) > 1000) {
                    skipMap.put(player, System.currentTimeMillis());
                    try {
                        loginUsers.getUser(player.getUuid());
                    } catch (Exception e) {
                        if(player.networkHandler.connection.getAddress() != null) {
                            player.networkHandler.sendPacket(new DisconnectS2CPacket(new LiteralText("invalid token, check your login status")));
                            player.networkHandler.disconnect(Text.of("invalid token, check your login status"));
                        }
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
                    XYZ xyz = new XYZ(player.getX(), player.getY(), player.getZ());
                    player.sendMessage(formatDeathMessage(player, xyz), false);
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
     * @param xyz
     *         等同于vec3d
     *
     * @return 格式化过后的信息
     *
     * @author 草二号机
     */
    public TranslatableText formatDeathMessage(ServerPlayerEntity player, XYZ xyz) {
        String dimension = dimensionTips.getDimension(player);
        return new TranslatableText("dead.deadIn", dimensionTips.getDimensionColor(dimension), dimensionTips.getDimensionName(dimension), xyz.getIntegerXYZ());
    }
}
