package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.ranking.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.mojang.brigadier.arguments.*;
import net.minecraft.command.argument.*;
import net.minecraft.server.*;
import net.minecraft.text.*;

import static net.minecraft.server.command.CommandManager.*;

public class RankingCommand extends ConfigurableCommand<RankingCommand> {
    @Override
    public RankingCommand register() {
        SharedVariables.commandRegister.register(literal("ranking").then(literal("create").then(literal("deaths").then(literal("setDisplay").executes(setDeathDisplay -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(setDeathDisplay), setDeathDisplay)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    showDeaths(SimpleCommandOperation.getServer(setDeathDisplay));
                    SimpleCommandOperation.sendFeedback(setDeathDisplay, formatObjectShow("deaths"));
                } catch (IllegalStateException e) {
                    SimpleCommandOperation.sendFeedback(setDeathDisplay, formatObjectNoDef("deaths"));
                }
            }
            return 0;
        }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(death -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(death), death)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    addDeathsScoreboard(SimpleCommandOperation.getServer(death), TextArgumentType.getTextArgument(death, "rankingDisplayName"));
                    showDeaths(SimpleCommandOperation.getServer(death));
                    SimpleCommandOperation.sendFeedback(death, formatObjectDefined("deaths"));
                } catch (Exception e) {

                }
            }
            return 0;
        })).then(argument("rankingDisplayName", StringArgumentType.greedyString()).executes(death -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(death), death)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    addDeathsScoreboard(SimpleCommandOperation.getServer(death), new TranslatableText(StringArgumentType.getString(death, "rankingDisplayName")));
                    showDeaths(SimpleCommandOperation.getServer(death));
                    SimpleCommandOperation.sendFeedback(death, formatObjectDefined("deaths"));
                } catch (Exception e) {

                }
            }
            return 0;
        })))).then(literal("tradesWithVillager").then(literal("setDisplay").executes(setTradeDisplay -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(setTradeDisplay), setTradeDisplay)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    showTrades(SimpleCommandOperation.getServer(setTradeDisplay));
                    SimpleCommandOperation.sendFeedback(setTradeDisplay, formatObjectShow("tradesWithVillager"));
                } catch (IllegalStateException e) {
                    SimpleCommandOperation.sendFeedback(setTradeDisplay, formatObjectNoDef("tradesWithVillager"));
                }
            }
            return 0;
        }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(trade -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(trade), trade)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    addTradesScoreboard(SimpleCommandOperation.getServer(trade), TextArgumentType.getTextArgument(trade, "rankingDisplayName"));
                    showTrades(SimpleCommandOperation.getServer(trade));
                    SimpleCommandOperation.sendFeedback(trade, formatObjectDefined("tradesWithVillager"));
                } catch (Exception e) {

                }
            }
            return 0;
        })).then(argument("rankingDisplayName", StringArgumentType.greedyString()).executes(trade -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(trade), trade)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    addTradesScoreboard(SimpleCommandOperation.getServer(trade), new TranslatableText(StringArgumentType.getString(trade, "rankingDisplayName")));
                    showTrades(SimpleCommandOperation.getServer(trade));
                    SimpleCommandOperation.sendFeedback(trade, formatObjectDefined("tradesWithVillager"));
                } catch (Exception e) {

                }
            }
            return 0;
        })))).then(literal("destroyBlocks").then(literal("setDisplay").executes(setDsyDisplay -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(setDsyDisplay), setDsyDisplay)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    showDestroys(SimpleCommandOperation.getServer(setDsyDisplay));
                    SimpleCommandOperation.sendFeedback(setDsyDisplay, formatObjectShow("destroyBlocks"));
                } catch (IllegalStateException e) {
                    SimpleCommandOperation.sendFeedback(setDsyDisplay, formatObjectNoDef("destroyBlocks"));
                }
            }
            return 0;
        }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(destroy -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(destroy), destroy)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    addDestroysScoreboard(SimpleCommandOperation.getServer(destroy), TextArgumentType.getTextArgument(destroy, "rankingDisplayName"));
                    showDestroys(SimpleCommandOperation.getServer(destroy));
                    SimpleCommandOperation.sendFeedback(destroy, formatObjectDefined("destroyBlocks"));
                } catch (Exception e) {

                }
            }
            return 0;
        })).then(argument("rankingDisplayName", StringArgumentType.greedyString()).executes(destroy -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(destroy), destroy)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    addDestroysScoreboard(SimpleCommandOperation.getServer(destroy), new TranslatableText(StringArgumentType.getString(destroy, "rankingDisplayName")));
                    showDestroys(SimpleCommandOperation.getServer(destroy));
                    SimpleCommandOperation.sendFeedback(destroy, formatObjectDefined("destroyBlocks"));
                } catch (Exception e) {

                }
            }
            return 0;
        })))).then(literal("onlineTimes").then(literal("setDisplay").executes(setOtsDisplay -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(setOtsDisplay), setOtsDisplay)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    showOnlineTime(SimpleCommandOperation.getServer(setOtsDisplay));
                    SimpleCommandOperation.sendFeedback(setOtsDisplay, formatObjectShow("onlineTime"));
                } catch (IllegalStateException e) {
                    SimpleCommandOperation.sendFeedback(setOtsDisplay, formatObjectNoDef("onlineTime"));
                }
            }
            return 0;
        }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(onlineTime -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(onlineTime), onlineTime)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    addOnlineTimeScoreboard(SimpleCommandOperation.getServer(onlineTime), TextArgumentType.getTextArgument(onlineTime, "rankingDisplayName"));
                    showOnlineTime(SimpleCommandOperation.getServer(onlineTime));
                    SimpleCommandOperation.sendFeedback(onlineTime, formatObjectDefined("onlineTime"));
                } catch (Exception e) {

                }
            }
            return 0;
        })).then((argument("rankingDisplayName", StringArgumentType.greedyString()).executes(onlineTime -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(onlineTime), onlineTime)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);

                try {
                    addOnlineTimeScoreboard(SimpleCommandOperation.getServer(onlineTime), new TranslatableText(StringArgumentType.getString(onlineTime, "rankingDisplayName")));
                    showOnlineTime(SimpleCommandOperation.getServer(onlineTime));
                    SimpleCommandOperation.sendFeedback(onlineTime, formatObjectDefined("onlineTime"));
                } catch (Exception e) {

                }
            }
            return 0;
        })))).then(literal("setScale").then(literal("second").executes(scaleSecond -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(scaleSecond), scaleSecond)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);
                setOnlineTimeScale("second");

                SimpleCommandOperation.sendFeedback(scaleSecond, formatTimeScale(SharedVariables.rankingOnlineTimeScale));
            }
            return 0;
        })).then(literal("minute").executes(scaleMinute -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(scaleMinute), scaleMinute)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);
                setOnlineTimeScale("minute");

                SimpleCommandOperation.sendFeedback(scaleMinute, formatTimeScale(SharedVariables.rankingOnlineTimeScale));
            }
            return 0;
        })).then(literal("hour").executes(scaleHour -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(scaleHour), scaleHour)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);
                setOnlineTimeScale("hour");

                SimpleCommandOperation.sendFeedback(scaleHour, formatTimeScale(SharedVariables.rankingOnlineTimeScale));
            }
            return 0;
        })).then(literal("day").executes(scaleDay -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(scaleDay), scaleDay)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);
                setOnlineTimeScale("day");

                SimpleCommandOperation.sendFeedback(scaleDay, formatTimeScale(SharedVariables.rankingOnlineTimeScale));
            }
            return 0;
        })).then(literal("month").executes(scaleMonth -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(scaleMonth), scaleMonth)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = true);
                setOnlineTimeScale("month");

                SimpleCommandOperation.sendFeedback(scaleMonth, formatTimeScale(SharedVariables.rankingOnlineTimeScale));
            }
            return 0;
        }))))).then(literal("disable").executes(disableRanking -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(disableRanking), disableRanking)) {
                SharedVariables.config.set("ranking", SharedVariables.enableRanking = false);
                SharedVariables.config.set("ranking_object", SharedVariables.rankingObject = "Nan");

                SimpleCommandOperation.sendFeedback(disableRanking, formatRankingDisabled());
            }
            return 0;
        })).then(literal("randomSwitch").executes(randomSwitch -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(randomSwitch), randomSwitch)) {
                try {
                    SharedVariables.config.set("ranking_object", SharedVariables.rankingObject = SharedVariables.getRandomRankingObject());
                } catch (Exception e) {

                }
            }
            return 0;
        }).then(literal("interval").executes(getInterval -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(getInterval), getInterval)) {
                SimpleCommandOperation.sendFeedback(getInterval, formatInterval(SharedVariables.rankingRandomSwitchInterval));
            }
            return 0;
        }).then(argument("ticks", IntegerArgumentType.integer(- 1)).executes(internalRandomSwitch -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(internalRandomSwitch), internalRandomSwitch)) {
                SharedVariables.config.set("ranking_random_switch_interval", SharedVariables.rankingRandomSwitchInterval = IntegerArgumentType.getInteger(internalRandomSwitch, "ticks"));
                SharedVariables.config.set("ranking_nodump", SharedVariables.rankingSwitchNoDump = false);

                SimpleCommandOperation.sendFeedback(internalRandomSwitch, formatSetInterval(SharedVariables.rankingRandomSwitchInterval));
            }
            return 0;
        }).then(literal("nodump").executes(nodumpSwitch -> {
            if (SharedVariables.commandApplyToPlayer(11, SimpleCommandOperation.getPlayer(nodumpSwitch), nodumpSwitch)) {
                SharedVariables.config.set("ranking_random_switch_interval", SharedVariables.rankingRandomSwitchInterval = IntegerArgumentType.getInteger(nodumpSwitch, "ticks"));
                SharedVariables.config.set("ranking_nodump", SharedVariables.rankingSwitchNoDump = true);

                SimpleCommandOperation.sendFeedback(nodumpSwitch, formatSetIntervalNoDump(SharedVariables.rankingRandomSwitchInterval));
            }
            return 0;
        }))))));

        return this;
    }

    public void setOnlineTimeScale(String scale) {
        if (! scale.equals(SharedVariables.rankingOnlineTimeScale)) {
            SharedVariables.rankingOnlineTimeScaleChanged = true;
            SharedVariables.config.set("ranking_online_time_scale", SharedVariables.rankingOnlineTimeScale = scale);
        }
    }

    public void showOnlineTime(MinecraftServer server) throws IllegalStateException {
        SharedVariables.showScoreboard(server, "modmdo.ots", "game.online.times");
    }

    public void addOnlineTimeScoreboard(MinecraftServer server, Text displayName) {
        SharedVariables.addScoreboard(server, displayName, "modmdo.ots");
    }

    public void showDestroys(MinecraftServer server) throws IllegalStateException {
        SharedVariables.showScoreboard(server, "modmdo.dsy", "destroy.blocks");
    }

    public void addDestroysScoreboard(MinecraftServer server, Text displayName) {
        SharedVariables.addScoreboard(server, displayName, "modmdo.dsy");
    }

    public void showTrades(MinecraftServer server) throws IllegalStateException {
        SharedVariables.showScoreboard(server, "modmdo.trd", "villager.trades");
    }

    public void addTradesScoreboard(MinecraftServer server, Text displayName) {
        SharedVariables.addScoreboard(server, displayName, "modmdo.trd");
    }

    public void showDeaths(MinecraftServer server) throws IllegalStateException {
        SharedVariables.showScoreboard(server, "modmdo.dts", "player.deaths");
    }

    public void addDeathsScoreboard(MinecraftServer server, Text displayName) {
        SharedVariables.addScoreboard(server, displayName, "modmdo.dts");
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
        SharedVariables.supportedRankingObjects.put("destroyBlocks", new Rank("destroyBlocks", "destroy.blocks", "modmdo.dsy", true));
        SharedVariables.supportedRankingObjects.put("tradesWithVillager", new Rank("tradesWithVillager", "villager.trades", "modmdo.trd", true));
        SharedVariables.supportedRankingObjects.put("deaths", new Rank("deaths", "player.deaths", "modmdo.dts", true));
        SharedVariables.supportedRankingObjects.put("onlineTimes", new Rank("onlineTimes", "online.times", "modmdo.ots", false));

        String onlineTimeScale = SharedVariables.config.getConfigString("ranking_online_time_scale");
        String ranking = SharedVariables.config.getConfigString("ranking_object");

        if (ranking != null)
            SharedVariables.rankingObject = ranking;

        if (onlineTimeScale != null)
            SharedVariables.rankingOnlineTimeScale = onlineTimeScale;

        try {
            SharedVariables.enableRanking = SharedVariables.config.getConfigBoolean("ranking");
        } catch (Exception e) {
            SharedVariables.config.set("ranking", SharedVariables.enableRanking);
        }

        try {
            SharedVariables.rankingRandomSwitchInterval = SharedVariables.config.getConfigInt("ranking_random_switch_interval");
        } catch (Exception e) {
            SharedVariables.config.set("ranking_random_switch_interval", SharedVariables.rankingRandomSwitchInterval);
        }

        return this;
    }
}
