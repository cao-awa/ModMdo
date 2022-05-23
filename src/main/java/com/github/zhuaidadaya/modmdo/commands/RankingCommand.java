package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.ranking.*;
import com.mojang.brigadier.arguments.*;
import net.minecraft.command.argument.*;
import net.minecraft.server.*;
import net.minecraft.text.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class RankingCommand extends ConfigurableCommand<RankingCommand> {
    @Override
    public RankingCommand register() {
        commandRegister.register(literal("ranking").then(literal("create").then(literal("deaths").then(literal("setDisplay").executes(setDeathDisplay -> {
            if (commandApplyToPlayer(11, getPlayer(setDeathDisplay), setDeathDisplay)) {
                config.set("ranking", enableRanking = true);

                try {
                    showDeaths(getServer(setDeathDisplay));
                    sendFeedback(setDeathDisplay, formatObjectShow("deaths"));
                } catch (IllegalStateException e) {
                    sendFeedback(setDeathDisplay, formatObjectNoDef("deaths"));
                }
            }
            return 0;
        }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(death -> {
            if (commandApplyToPlayer(11, getPlayer(death), death)) {
                config.set("ranking", enableRanking = true);

                try {
                    addDeathsScoreboard(getServer(death), TextArgumentType.getTextArgument(death, "rankingDisplayName"));
                    showDeaths(getServer(death));
                    sendFeedback(death, formatObjectDefined("deaths"));
                } catch (Exception e) {

                }
            }
            return 0;
        })).then(argument("rankingDisplayName", StringArgumentType.greedyString()).executes(death -> {
            if (commandApplyToPlayer(11, getPlayer(death), death)) {
                config.set("ranking", enableRanking = true);

                try {
                    addDeathsScoreboard(getServer(death), new TranslatableText(StringArgumentType.getString(death, "rankingDisplayName")));
                    showDeaths(getServer(death));
                    sendFeedback(death, formatObjectDefined("deaths"));
                } catch (Exception e) {

                }
            }
            return 0;
        })))).then(literal("tradesWithVillager").then(literal("setDisplay").executes(setTradeDisplay -> {
            if (commandApplyToPlayer(11, getPlayer(setTradeDisplay), setTradeDisplay)) {
                config.set("ranking", enableRanking = true);

                try {
                    showTrades(getServer(setTradeDisplay));
                    sendFeedback(setTradeDisplay, formatObjectShow("tradesWithVillager"));
                } catch (IllegalStateException e) {
                    sendFeedback(setTradeDisplay, formatObjectNoDef("tradesWithVillager"));
                }
            }
            return 0;
        }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(trade -> {
            if (commandApplyToPlayer(11, getPlayer(trade), trade)) {
                config.set("ranking", enableRanking = true);

                try {
                    addTradesScoreboard(getServer(trade), TextArgumentType.getTextArgument(trade, "rankingDisplayName"));
                    showTrades(getServer(trade));
                    sendFeedback(trade, formatObjectDefined("tradesWithVillager"));
                } catch (Exception e) {

                }
            }
            return 0;
        })).then(argument("rankingDisplayName", StringArgumentType.greedyString()).executes(trade -> {
            if (commandApplyToPlayer(11, getPlayer(trade), trade)) {
                config.set("ranking", enableRanking = true);

                try {
                    addTradesScoreboard(getServer(trade), new TranslatableText(StringArgumentType.getString(trade, "rankingDisplayName")));
                    showTrades(getServer(trade));
                    sendFeedback(trade, formatObjectDefined("tradesWithVillager"));
                } catch (Exception e) {

                }
            }
            return 0;
        })))).then(literal("destroyBlocks").then(literal("setDisplay").executes(setDsyDisplay -> {
            if (commandApplyToPlayer(11, getPlayer(setDsyDisplay), setDsyDisplay)) {
                config.set("ranking", enableRanking = true);

                try {
                    showDestroys(getServer(setDsyDisplay));
                    sendFeedback(setDsyDisplay, formatObjectShow("destroyBlocks"));
                } catch (IllegalStateException e) {
                    sendFeedback(setDsyDisplay, formatObjectNoDef("destroyBlocks"));
                }
            }
            return 0;
        }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(destroy -> {
            if (commandApplyToPlayer(11, getPlayer(destroy), destroy)) {
                config.set("ranking", enableRanking = true);

                try {
                    addDestroysScoreboard(getServer(destroy), TextArgumentType.getTextArgument(destroy, "rankingDisplayName"));
                    showDestroys(getServer(destroy));
                    sendFeedback(destroy, formatObjectDefined("destroyBlocks"));
                } catch (Exception e) {

                }
            }
            return 0;
        })).then(argument("rankingDisplayName", StringArgumentType.greedyString()).executes(destroy -> {
            if (commandApplyToPlayer(11, getPlayer(destroy), destroy)) {
                config.set("ranking", enableRanking = true);

                try {
                    addDestroysScoreboard(getServer(destroy), new TranslatableText(StringArgumentType.getString(destroy, "rankingDisplayName")));
                    showDestroys(getServer(destroy));
                    sendFeedback(destroy, formatObjectDefined("destroyBlocks"));
                } catch (Exception e) {

                }
            }
            return 0;
        })))).then(literal("onlineTimes").then(literal("setDisplay").executes(setOtsDisplay -> {
            if (commandApplyToPlayer(11, getPlayer(setOtsDisplay), setOtsDisplay)) {
                config.set("ranking", enableRanking = true);

                try {
                    showOnlineTime(getServer(setOtsDisplay));
                    sendFeedback(setOtsDisplay, formatObjectShow("onlineTime"));
                } catch (IllegalStateException e) {
                    sendFeedback(setOtsDisplay, formatObjectNoDef("onlineTime"));
                }
            }
            return 0;
        }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(onlineTime -> {
            if (commandApplyToPlayer(11, getPlayer(onlineTime), onlineTime)) {
                config.set("ranking", enableRanking = true);

                try {
                    addOnlineTimeScoreboard(getServer(onlineTime), TextArgumentType.getTextArgument(onlineTime, "rankingDisplayName"));
                    showOnlineTime(getServer(onlineTime));
                    sendFeedback(onlineTime, formatObjectDefined("onlineTime"));
                } catch (Exception e) {

                }
            }
            return 0;
        })).then((argument("rankingDisplayName", StringArgumentType.greedyString()).executes(onlineTime -> {
            if (commandApplyToPlayer(11, getPlayer(onlineTime), onlineTime)) {
                config.set("ranking", enableRanking = true);

                try {
                    addOnlineTimeScoreboard(getServer(onlineTime), new TranslatableText(StringArgumentType.getString(onlineTime, "rankingDisplayName")));
                    showOnlineTime(getServer(onlineTime));
                    sendFeedback(onlineTime, formatObjectDefined("onlineTime"));
                } catch (Exception e) {

                }
            }
            return 0;
        })))).then(literal("setScale").then(literal("second").executes(scaleSecond -> {
            if (commandApplyToPlayer(11, getPlayer(scaleSecond), scaleSecond)) {
                config.set("ranking", enableRanking = true);
                setOnlineTimeScale("second");

                sendFeedback(scaleSecond, formatTimeScale(rankingOnlineTimeScale));
            }
            return 0;
        })).then(literal("minute").executes(scaleMinute -> {
            if (commandApplyToPlayer(11, getPlayer(scaleMinute), scaleMinute)) {
                config.set("ranking", enableRanking = true);
                setOnlineTimeScale("minute");

                sendFeedback(scaleMinute, formatTimeScale(rankingOnlineTimeScale));
            }
            return 0;
        })).then(literal("hour").executes(scaleHour -> {
            if (commandApplyToPlayer(11, getPlayer(scaleHour), scaleHour)) {
                config.set("ranking", enableRanking = true);
                setOnlineTimeScale("hour");

                sendFeedback(scaleHour, formatTimeScale(rankingOnlineTimeScale));
            }
            return 0;
        })).then(literal("day").executes(scaleDay -> {
            if (commandApplyToPlayer(11, getPlayer(scaleDay), scaleDay)) {
                config.set("ranking", enableRanking = true);
                setOnlineTimeScale("day");

                sendFeedback(scaleDay, formatTimeScale(rankingOnlineTimeScale));
            }
            return 0;
        })).then(literal("month").executes(scaleMonth -> {
            if (commandApplyToPlayer(11, getPlayer(scaleMonth), scaleMonth)) {
                config.set("ranking", enableRanking = true);
                setOnlineTimeScale("month");

                sendFeedback(scaleMonth, formatTimeScale(rankingOnlineTimeScale));
            }
            return 0;
        }))))).then(literal("disable").executes(disableRanking -> {
            if (commandApplyToPlayer(11, getPlayer(disableRanking), disableRanking)) {
                config.set("ranking", enableRanking = false);
                config.set("ranking_object", rankingObject = "Nan");

                sendFeedback(disableRanking, formatRankingDisabled());
            }
            return 0;
        })).then(literal("randomSwitch").executes(randomSwitch -> {
            if (commandApplyToPlayer(11, getPlayer(randomSwitch), randomSwitch)) {
                try {
                    config.set("ranking_object", rankingObject = getRandomRankingObject());
                } catch (Exception e) {

                }
            }
            return 0;
        }).then(literal("interval").executes(getInterval -> {
            if (commandApplyToPlayer(11, getPlayer(getInterval), getInterval)) {
                sendFeedback(getInterval, formatInterval(rankingRandomSwitchInterval));
            }
            return 0;
        }).then(argument("ticks", IntegerArgumentType.integer(- 1)).executes(internalRandomSwitch -> {
            if (commandApplyToPlayer(11, getPlayer(internalRandomSwitch), internalRandomSwitch)) {
                config.set("ranking_random_switch_interval", rankingRandomSwitchInterval = IntegerArgumentType.getInteger(internalRandomSwitch, "ticks"));
                config.set("ranking_nodump", rankingSwitchNoDump = false);

                sendFeedback(internalRandomSwitch, formatSetInterval(rankingRandomSwitchInterval));
            }
            return 0;
        }).then(literal("nodump").executes(nodumpSwitch -> {
            if (commandApplyToPlayer(11, getPlayer(nodumpSwitch), nodumpSwitch)) {
                config.set("ranking_random_switch_interval", rankingRandomSwitchInterval = IntegerArgumentType.getInteger(nodumpSwitch, "ticks"));
                config.set("ranking_nodump", rankingSwitchNoDump = true);

                sendFeedback(nodumpSwitch, formatSetIntervalNoDump(rankingRandomSwitchInterval));
            }
            return 0;
        }))))));

        return this;
    }

    public void setOnlineTimeScale(String scale) {
        if (! scale.equals(rankingOnlineTimeScale)) {
            rankingOnlineTimeScaleChanged = true;
            config.set("ranking_online_time_scale", rankingOnlineTimeScale = scale);
        }
    }

    public void showOnlineTime(MinecraftServer server) throws IllegalStateException {
        showScoreboard(server, "modmdo.ots", "game.online.times");
    }

    public void addOnlineTimeScoreboard(MinecraftServer server, Text displayName) {
        addScoreboard(server, displayName, "modmdo.ots");
    }

    public void showDestroys(MinecraftServer server) throws IllegalStateException {
        showScoreboard(server, "modmdo.dsy", "destroy.blocks");
    }

    public void addDestroysScoreboard(MinecraftServer server, Text displayName) {
        addScoreboard(server, displayName, "modmdo.dsy");
    }

    public void showTrades(MinecraftServer server) throws IllegalStateException {
        showScoreboard(server, "modmdo.trd", "villager.trades");
    }

    public void addTradesScoreboard(MinecraftServer server, Text displayName) {
        addScoreboard(server, displayName, "modmdo.trd");
    }

    public void showDeaths(MinecraftServer server) throws IllegalStateException {
        showScoreboard(server, "modmdo.dts", "player.deaths");
    }

    public void addDeathsScoreboard(MinecraftServer server, Text displayName) {
        addScoreboard(server, displayName, "modmdo.dts");
    }

    public TranslatableText formatObjectNoDef(String ranking) {
        return new TranslatableText("ranking.no.def", ranking);
    }

    public TranslatableText formatObjectDefined(String ranking) {
        return new TranslatableText("ranking.defined", ranking);
    }

    public TranslatableText formatObjectShow(String ranking) {
        return new TranslatableText("ranking.show", ranking);
    }

    public TranslatableText formatRankingDisabled() {
        return new TranslatableText("ranking.disabled");
    }

    public TranslatableText formatInterval(int interval) {
        return new TranslatableText("ranking.switch.interval", interval);
    }

    public TranslatableText formatSetInterval(int interval) {
        return new TranslatableText("ranking.switch.set.interval", interval);
    }

    public TranslatableText formatSetIntervalNoDump(int interval) {
        return new TranslatableText("ranking.switch.set.interval.nodump", interval);
    }

    public TranslatableText formatTimeScale(String scale) {
        return new TranslatableText("ranking.online.times.scale." + scale);
    }

    @Override
    public RankingCommand init() {
        supportedRankingObjects.put("destroyBlocks", new Rank("destroyBlocks", "destroy.blocks", "modmdo.dsy", true));
        supportedRankingObjects.put("tradesWithVillager", new Rank("tradesWithVillager", "villager.trades", "modmdo.trd", true));
        supportedRankingObjects.put("deaths", new Rank("deaths", "player.deaths", "modmdo.dts", true));
        supportedRankingObjects.put("onlineTimes", new Rank("onlineTimes", "online.times", "modmdo.ots", false));

        String onlineTimeScale = config.getConfigString("ranking_online_time_scale");
        String ranking = config.getConfigString("ranking_object");

        if (ranking != null)
            rankingObject = ranking;

        if (onlineTimeScale != null)
            rankingOnlineTimeScale = onlineTimeScale;

        try {
            enableRanking = config.getConfigBoolean("ranking");
        } catch (Exception e) {
            config.set("ranking", enableRanking);
        }

        try {
            rankingRandomSwitchInterval = config.getConfigInt("ranking_random_switch_interval");
        } catch (Exception e) {
            config.set("ranking_random_switch_interval", rankingRandomSwitchInterval);
        }

        return this;
    }
}
