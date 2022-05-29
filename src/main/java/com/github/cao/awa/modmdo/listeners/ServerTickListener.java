package com.github.cao.awa.modmdo.listeners;

import com.github.cao.awa.modmdo.network.forwarder.process.*;
import com.github.cao.awa.modmdo.ranking.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.scoreboard.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.json.*;

import java.io.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ServerTickListener {
    private MinecraftServer server;
    private long lastIntervalActive = System.currentTimeMillis();
    private int randomRankingSwitchTick = 0;

    /**
     * 添加服务器监听, 每tick结束以后执行一些需要的操作
     *
     * @author 草二号机
     */
    public void listener() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            this.server = server;
            PlayerManager players = server.getPlayerManager();

            randomRankingSwitchTick++;

            EntrustExecution.tryTemporary(() -> eachPlayer(players));

            for (ModMdoDataProcessor processor : modmdoConnections) {
                processor.tick(server);
            }
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            event.submitGameTickStart(server);
        });

        Thread subListener = new Thread(() -> {
            while (server == null) {
                EntrustExecution.tryTemporary(() -> TimeUtil.barricade(100));
            }
            while (server.isRunning()) {
                EntrustExecution.tryTemporary(() -> {
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

                    EntrustExecution.tryTemporary(() -> TimeUtil.barricade(20));
                });
            }
        });

        subListener.setName("ModMdo sub listener thread");

        subListener.start();
    }

    public void updateOtherRankings(MinecraftServer server, PlayerManager manager) {
        EntrustExecution.tryFor(manager.getPlayerList(), player -> {
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
                case "destroyBlocks" -> updateDestroyBlocks(server, player, stat);
                case "tradesWithVillager" -> updateCustomRanking("minecraft:traded_with_villager", server, player, stat);
                case "deaths" -> updateCustomRanking("minecraft:deaths", server, player, stat);
                case "gameOnlineTimes" -> updateGameOnlineTime(stat, server, player);
            }
        });
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
                count = custom.getInt(countObject);
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

        }
    }

    public void updateGameOnlineTime(JSONObject stat, MinecraftServer server, ServerPlayerEntity player) {
        User user = users.getUser(player);

        if (rankingOnlineTimeScaleChanged) {
            updateGameOnlineTime(server, user.getName(), stat);
            rankingOnlineTimeScaleChanged = false;
        }
    }

    public void updateGameOnlineTime(MinecraftServer server, String name, JSONObject stat) {
        User user = users.getUserFromName(name);
        if (user != null) {
            ServerScoreboard scoreboard = server.getScoreboard();
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(name, scoreboard.getObjective("modmdo.ots"));
            long gameTime = stat.getJSONObject("minecraft:custom").getLong("minecraft:play_time") * 50;
            long showOnlineTime;
            showOnlineTime = switch (rankingOnlineTimeScale) {
                case "second" -> TimeUtil.formatSecond(gameTime);
                case "hour" ->  TimeUtil.formatHour(gameTime);
                case "day" ->  TimeUtil.formatDay(gameTime);
                case "month" -> TimeUtil.formatMonth(gameTime);
                default -> TimeUtil.formatMinute(gameTime);
            };
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
