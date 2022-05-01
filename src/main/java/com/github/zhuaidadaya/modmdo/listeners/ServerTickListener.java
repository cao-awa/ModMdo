package com.github.zhuaidadaya.modmdo.listeners;

import com.github.zhuaidadaya.modmdo.ranking.Rank;
import com.github.zhuaidadaya.modmdo.simple.vec.XYZ;
import com.github.zhuaidadaya.modmdo.storage.Variables;
import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import com.github.zhuaidadaya.modmdo.utils.usr.User;
import com.github.zhuaidadaya.modmdo.utils.dimension.DimensionUtil;
import com.github.zhuaidadaya.modmdo.utils.times.TimeUtil;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ServerTickListener {
    private MinecraftServer server;
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

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            this.server = server;
            PlayerManager players = server.getPlayerManager();

            randomRankingSwitchTick++;

            Variables.server = server;

            try {
                eachPlayer(players);
            } catch (Exception e) {

            }
        });

        Thread subListener = new Thread(() -> {
            while (server == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            }
            while (server.isRunning()) {
                try {
                    PlayerManager players = server.getPlayerManager();

                    updateRankingShow(server);

                    if (System.currentTimeMillis() - lastIntervalActive > 1000) {
                        if (rankingIsStatObject(rankingObject)) {
                            updateOtherRankings(server, players);
                        }
                        lastIntervalActive = System.currentTimeMillis();
                    }

                    if (rankingRandomSwitchInterval != - 1) {
                        if (randomRankingSwitchTick > rankingRandomSwitchInterval) {
                            if (rankingSwitchNoDump)
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

        subListener.start();
    }

    public void updateOtherRankings(MinecraftServer server, PlayerManager manager) {
        for (ServerPlayerEntity player : manager.getPlayerList()) {
            User user = users.getUser(player);
            if (! user.isDummyPlayer()) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(getServerLevelPath(server) + "stats/" + player.getUuid().toString() + ".json"));

                    player.getStatHandler().save();

                    String cache;
                    StringBuilder builder = new StringBuilder();
                    while ((cache = reader.readLine()) != null) {
                        builder.append(cache);
                    }

                    JSONObject source = new JSONObject(builder.toString());
                    JSONObject stat = source.getJSONObject("stats");

                    switch (rankingObject) {
                        case "destroyBlocks" -> {
                            updateDestroyBlocks(server, player, stat);
                        }
                        case "tradesWithVillager" -> {
                            updateCustomRanking("minecraft:traded_with_villager", server, player, stat);
                        }
                        case "deaths" -> {
                            updateCustomRanking("minecraft:deaths", server, player, stat);
                        }
                        case "gameOnlineTimes" -> {
                            updateGameOnlineTime(stat, server, player);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateCustomRanking(String object, MinecraftServer server, ServerPlayerEntity player, JSONObject stat) {
        try {
            JSONObject custom = stat.getJSONObject("minecraft:custom");

            String scoreboardObject = "";
            String countObject = "";

            switch (object) {
                case "minecraft:traded_with_villager" -> {
                    scoreboardObject = "modmdo.trd";
                    countObject = "minecraft:traded_with_villager";
                }
                case "minecraft:deaths" -> {
                    scoreboardObject = "modmdo.dts";
                    countObject = "minecraft:deaths";
                }
            }
            int count = 0;
            try {
                custom.getInt(countObject);
            } catch (Exception e) {

            }

            ServerScoreboard scoreboard = server.getScoreboard();
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(player.getName().asString(), scoreboard.getObjective(scoreboardObject));

            scoreboardPlayerScore.setScore(count);
            scoreboard.updateScore(scoreboardPlayerScore);
        } catch (Exception e) {

        }
    }

    public void updateDestroyBlocks(MinecraftServer server, ServerPlayerEntity player, JSONObject stat) {
        try {
            int minedCount = 0;

            try {
                JSONObject mined = stat.getJSONObject("minecraft:mined");

                for (String s : mined.keySet()) {
                    minedCount += mined.getInt(s);
                }
            } catch (Exception e) {

            }

            ServerScoreboard scoreboard = server.getScoreboard();
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(player.getName().asString(), scoreboard.getObjective("modmdo.dsy"));

            scoreboardPlayerScore.setScore(minedCount);
            scoreboard.updateScore(scoreboardPlayerScore);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateGameOnlineTime(JSONObject stat, MinecraftServer server, ServerPlayerEntity player) {
        User user = users.getUser(player);
        ServerScoreboard scoreboard = server.getScoreboard();
        ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(player.getName().asString(), scoreboard.getObjective("modmdo.ots"));

        for (ScoreboardPlayerScore score : scoreboardPlayerScore.getScoreboard().getAllPlayerScores(scoreboardPlayerScore.getObjective())) {
            User each = users.getUserFromName(score.getPlayerName());
            if (! each.isDummyPlayer()) {
                if (rankingOnlineTimeScaleChanged) {
                    updateGameOnlineTime(server, user.getName(), stat);
                    rankingOnlineTimeScaleChanged = false;
                }
            } else {
                scoreboard.resetPlayerScore(score.getPlayerName(), score.getObjective());
            }
        }
        if (! user.isDummyPlayer()) {
            updateGameOnlineTime(server, user.getName(), stat);
        }
    }

    public void updateGameOnlineTime(MinecraftServer server, String name, JSONObject stat) {
        User user = users.getUserFromName(name);
        if (user != null) {
            ServerScoreboard scoreboard = server.getScoreboard();
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(name, scoreboard.getObjective("modmdo.ots"));
            long gameTime = stat.getJSONObject("minecraft:custom").getLong("minecraft:play_time") * 50;
            long showOnlineTime;
            switch (rankingOnlineTimeScale) {
                case "second" -> {
                    showOnlineTime = TimeUtil.formatSecond(gameTime);
                }
                case "hour" -> {
                    showOnlineTime = TimeUtil.formatHour(gameTime);
                }
                case "day" -> {
                    showOnlineTime = TimeUtil.formatDay(gameTime);
                }
                case "month" -> {
                    showOnlineTime = TimeUtil.formatMonth(gameTime);
                }
                default -> {
                    showOnlineTime = TimeUtil.formatMinute(gameTime);
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
     * @author 草awa
     * @author 草二号机
     */
    public void eachPlayer(PlayerManager players) {
        for (ServerPlayerEntity player : players.getPlayerList()) {
            if (needSync) {
                player.getInventory().updateItems();
            }
            if (modMdoType == ModMdoType.SERVER & modmdoWhiteList) {
                checkLoginStat(player, players);
            }
            if (enableDeadMessage) {
                detectPlayerDead(player);
            }
        }
    }

    public void checkLoginStat(ServerPlayerEntity player, PlayerManager manager) {
        EntrustExecution.tryTemporary(() -> {
            if (modmdoWhiteList) {
                if (!whitelist.get(player.getName().asString()).identifier().equals(loginUsers.getUser(player).getIdentifier())) {
                    throw new Exception();
                }
            }
        }, () -> {
            if (player.networkHandler.connection.isOpen()) {
                player.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted"));
            }
        });
    }

    /**
     * 检测玩家的死亡状态, 如果死亡时间为1则发送当时的坐标和维度信息
     * (如果该玩家愿意接收才发送)
     *
     * @param player
     *         玩家
     * @author 草二号机
     */
    public void detectPlayerDead(ServerPlayerEntity player) {
        try {
            if (isUserDeadMessageReceive(player.getUuid()) & enableDeadMessage) {
                if (player.deathTime == 1) {
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
     * @return 格式化过后的信息
     * @author 草二号机
     */
    public TranslatableText formatDeathMessage(ServerPlayerEntity player, XYZ xyz) {
        String dimension = DimensionUtil.getDimension(player);
        return new TranslatableText("dead.deadIn", DimensionUtil.getDimensionColor(dimension), DimensionUtil.getDimensionName(dimension), xyz.getIntegerXYZ());
    }

    public void updateRankingShow(MinecraftServer server) {
        ServerScoreboard scoreboard = server.getScoreboard();

        if (enableRanking) {
            if (scoreboard.containsObjective("modmdo.dts")) {
                rankingObjects.add(new Rank("deaths", "player.deaths", "modmdo.dts", true));
            }
            if (scoreboard.containsObjective("modmdo.dsy")) {
                rankingObjects.add(new Rank("destroyBlocks", "destroy.blocks", "modmdo.dsy", true));
            }
            if (scoreboard.containsObjective("modmdo.ots")) {
                rankingObjects.add(new Rank("onlineTimes", "online.times", "modmdo.ots", false));
            }
            if (scoreboard.containsObjective("modmdo.trd")) {
                rankingObjects.add(new Rank("tradesWithVillager", "villager.trades", "modmdo.trd", true));
            }

            switch (rankingObject) {
                case "deaths" -> showScoreboard(server, "modmdo.dts", "deaths");
                case "onlineTimes" -> showScoreboard(server, "modmdo.ots", "onlineTimes");
                case "destroyBlocks" -> showScoreboard(server, "modmdo.dsy", "destroyBlocks");
                case "tradesWithVillager" -> showScoreboard(server, "modmdo.trd", "tradesWithVillager");
            }
        } else {
            if (scoreboard.containsObjective("modmdo.dts"))
                scoreboard.removeObjective(scoreboard.getObjective("modmdo.dts"));
            if (scoreboard.containsObjective("modmdo.dsy"))
                scoreboard.removeObjective(scoreboard.getObjective("modmdo.dsy"));
            if (scoreboard.containsObjective("modmdo.ots"))
                scoreboard.removeObjective(scoreboard.getObjective("modmdo.ots"));
            if (scoreboard.containsObjective("modmdo.trd"))
                scoreboard.removeObjective(scoreboard.getObjective("modmdo.trd"));
        }
    }
}
