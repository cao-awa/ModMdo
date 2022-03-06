package com.github.zhuaidadaya.modMdo.commands.ranking;

import com.github.zhuaidadaya.modMdo.commands.ConfigurableCommand;
import com.github.zhuaidadaya.modMdo.commands.SimpleCommandOperation;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RankingCommand extends SimpleCommandOperation implements ConfigurableCommand {
    @Override
    public void register() {
        init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("ranking").then(literal("objects").then(literal("deaths").then(literal("setDisplay").executes(setDeathDisplay -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(setDeathDisplay), this, setDeathDisplay)) {
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
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(death), this, death)) {
                    config.set("ranking", enableRanking = true);

                    try {
                        addDeathsScoreboard(getServer(death), TextArgumentType.getTextArgument(death, "rankingDisplayName"));
                        showDeaths(getServer(death));
                        sendFeedback(death, formatObjectDefined("deaths"));
                    } catch (Exception e) {

                    }
                }
                return 0;
            })))).then(literal("tradesWithVillager").then(literal("setDisplay").executes(setTradeDisplay -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(setTradeDisplay), this, setTradeDisplay)) {
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
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(trade), this, trade)) {
                    config.set("ranking", enableRanking = true);

                    try {
                        addTradesScoreboard(getServer(trade), TextArgumentType.getTextArgument(trade, "rankingDisplayName"));
                        showTrades(getServer(trade));
                        sendFeedback(trade, formatObjectDefined("tradesWithVillager"));
                    } catch (Exception e) {

                    }
                }
                return 0;
            })))).then(literal("destroyBlocks").then(literal("setDisplay").executes(setDsyDisplay -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(setDsyDisplay), this, setDsyDisplay)) {
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
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(destroy), this, destroy)) {
                    config.set("ranking", enableRanking = true);

                    try {
                        addDestroysScoreboard(getServer(destroy), TextArgumentType.getTextArgument(destroy, "rankingDisplayName"));
                        showDestroys(getServer(destroy));
                        sendFeedback(destroy, formatObjectDefined("destroyBlocks"));
                    } catch (Exception e) {

                    }
                }
                return 0;
            })))).then(literal("onlineTimes").then(literal("setDisplay").executes(setOtsDisplay -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(setOtsDisplay), this, setOtsDisplay)) {
                    config.set("ranking", enableRanking = true);

                    try {
                        showOnlineTime(getServer(setOtsDisplay));
                        sendFeedback(setOtsDisplay, formatObjectShow("onlineTimes"));
                    } catch (IllegalStateException e) {
                        sendFeedback(setOtsDisplay, formatObjectNoDef("onlineTimes"));
                    }
                }
                return 0;
            }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(onlineTime -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(onlineTime), this, onlineTime)) {
                    config.set("ranking", enableRanking = true);

                    try {
                        addOnlineTimeScoreboard(getServer(onlineTime), TextArgumentType.getTextArgument(onlineTime, "rankingDisplayName"));
                        showOnlineTime(getServer(onlineTime));
                        sendFeedback(onlineTime, formatObjectDefined("onlineTimes"));
                    } catch (Exception e) {

                    }
                }
                return 0;
            }))).then(literal("setScale").then(literal("second").executes(scaleSecond -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleSecond), this, scaleSecond)) {
                    config.set("ranking", enableRanking = true);
                    setOnlineTimeScale("second");

                    sendFeedback(scaleSecond,formatTimeScale(rankingOnlineTimeScale));
                }
                return 0;
            })).then(literal("minute").executes(scaleMinute -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleMinute), this, scaleMinute)) {
                    config.set("ranking", enableRanking = true);
                    setOnlineTimeScale("minute");

                    sendFeedback(scaleMinute,formatTimeScale(rankingOnlineTimeScale));
                }
                return 0;
            })).then(literal("hour").executes(scaleHour -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleHour), this, scaleHour)) {
                    config.set("ranking", enableRanking = true);
                    setOnlineTimeScale("hour");

                    sendFeedback(scaleHour,formatTimeScale(rankingOnlineTimeScale));
                }
                return 0;
            })).then(literal("day").executes(scaleDay -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleDay), this, scaleDay)) {
                    config.set("ranking", enableRanking = true);
                    setOnlineTimeScale("day");

                    sendFeedback(scaleDay,formatTimeScale(rankingOnlineTimeScale));
                }
                return 0;
            })).then(literal("month").executes(scaleMonth -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleMonth), this, scaleMonth)) {
                    config.set("ranking", enableRanking = true);
                    setOnlineTimeScale("month");

                    sendFeedback(scaleMonth,formatTimeScale(rankingOnlineTimeScale));
                }
                return 0;
            })))).then(literal("gameOnlineTimes").then(literal("setDisplay").executes(setOtsDisplay -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(setOtsDisplay), this, setOtsDisplay)) {
                    config.set("ranking", enableRanking = true);

                    try {
                        showGameOnlineTime(getServer(setOtsDisplay));
                        sendFeedback(setOtsDisplay, formatObjectShow("gameOnlineTime"));
                    } catch (IllegalStateException e) {
                        sendFeedback(setOtsDisplay, formatObjectNoDef("gameOnlineTime"));
                    }
                }
                return 0;
            }).then(argument("rankingDisplayName", TextArgumentType.text()).executes(onlineTime -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(onlineTime), this, onlineTime)) {
                    config.set("ranking", enableRanking = true);

                    try {
                        addGameOnlineTimeScoreboard(getServer(onlineTime), TextArgumentType.getTextArgument(onlineTime, "rankingDisplayName"));
                        showGameOnlineTime(getServer(onlineTime));
                        sendFeedback(onlineTime, formatObjectDefined("gameOnlineTime"));
                    } catch (Exception e) {

                    }
                }
                return 0;
            }))).then(literal("setScale").then(literal("second").executes(scaleSecond -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleSecond), this, scaleSecond)) {
                    config.set("ranking", enableRanking = true);
                    setGameOnlineTimeScale("second");

                    sendFeedback(scaleSecond,formatTimeScale(rankingGameOnlineTimeScale));
                }
                return 0;
            })).then(literal("minute").executes(scaleMinute -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleMinute), this, scaleMinute)) {
                    config.set("ranking", enableRanking = true);
                    setGameOnlineTimeScale("minute");

                    sendFeedback(scaleMinute,formatTimeScale(rankingGameOnlineTimeScale));
                }
                return 0;
            })).then(literal("hour").executes(scaleHour -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleHour), this, scaleHour)) {
                    config.set("ranking", enableRanking = true);
                    setGameOnlineTimeScale("hour");

                    sendFeedback(scaleHour,formatTimeScale(rankingGameOnlineTimeScale));
                }
                return 0;
            })).then(literal("day").executes(scaleDay -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleDay), this, scaleDay)) {
                    config.set("ranking", enableRanking = true);
                    setGameOnlineTimeScale("day");

                    sendFeedback(scaleDay,formatTimeScale(rankingGameOnlineTimeScale));
                }
                return 0;
            })).then(literal("month").executes(scaleMonth -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(scaleMonth), this, scaleMonth)) {
                    config.set("ranking", enableRanking = true);
                    setGameOnlineTimeScale("month");

                    sendFeedback(scaleMonth,formatTimeScale(rankingGameOnlineTimeScale));
                }
                return 0;
            }))))).then(literal("disable").executes(disableRanking -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(disableRanking), this, disableRanking)) {
                    config.set("ranking", enableRanking = false);
                    config.set("ranking_object", rankingObject = "Nan");

                    sendFeedback(disableRanking, formatRankingDisabled());
                }
                return 0;
            })).then(literal("randomSwitch").executes(randomSwitch -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(randomSwitch), this, randomSwitch)) {
                    try {
                        config.set("ranking_object", rankingObject = getRandomRankingObject());
                    } catch (Exception e) {

                    }
                }
                return 0;
            }).then(literal("interval").executes(getInterval -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(getInterval), this, getInterval)) {
                    sendFeedback(getInterval, formatInterval(rankingRandomSwitchInterval));
                }
                return 0;
            }).then(argument("ticks", IntegerArgumentType.integer(- 1)).executes(internalRandomSwitch -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(internalRandomSwitch), this, internalRandomSwitch)) {
                    config.set("ranking_random_switch_interval", rankingRandomSwitchInterval = IntegerArgumentType.getInteger(internalRandomSwitch, "ticks"));
                    config.set("ranking_nodump", rankingSwitchNoDump = false);

                    sendFeedback(internalRandomSwitch, formatSetInterval(rankingRandomSwitchInterval));
                }
                return 0;
            }).then(literal("nodump").executes(nodumpSwitch -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_RANKING, getPlayer(nodumpSwitch), this, nodumpSwitch)) {
                    config.set("ranking_random_switch_interval", rankingRandomSwitchInterval = IntegerArgumentType.getInteger(nodumpSwitch, "ticks"));
                    config.set("ranking_nodump", rankingSwitchNoDump = true);

                    sendFeedback(nodumpSwitch, formatSetIntervalNoDump(rankingRandomSwitchInterval));
                }
                return 0;
            }))))));
        });
    }

    public void setOnlineTimeScale(String scale) {
        if (!scale.equals(rankingOnlineTimeScale)) {
            rankingOnlineTimeScaleChanged = true;
            config.set("ranking_online_time_scale", rankingOnlineTimeScale = scale);
        }
    }

    public void setGameOnlineTimeScale(String scale) {
        if (!scale.equals(rankingGameOnlineTimeScale)) {
            rankingGameOnlineTimeScaleChanged = true;
            config.set("ranking_game_online_time_scale", rankingGameOnlineTimeScale = scale);
        }
    }

    public void showOnlineTime(MinecraftServer server) throws IllegalStateException {
        ServerScoreboard scoreboard = server.getScoreboard();

        if(scoreboard.containsObjective("modmdo.ots")) {
            config.set("ranking_object", rankingObject = "online.times");

            ((Scoreboard) scoreboard).setObjectiveSlot(1, scoreboard.getObjective("modmdo.ots"));
        } else {
            throw new IllegalStateException();
        }
    }

    public void addOnlineTimeScoreboard(MinecraftServer server, Text displayName) {
        ServerScoreboard scoreboard = server.getScoreboard();
        if(scoreboard.containsObjective("modmdo.ots")) {
            scoreboard.removeObjective(scoreboard.getObjective("modmdo.ots"));
        }
        scoreboard.addObjective("modmdo.ots", ScoreboardCriterion.DUMMY, displayName, ScoreboardCriterion.DUMMY.getDefaultRenderType());
    }

    public void showGameOnlineTime(MinecraftServer server) throws IllegalStateException {
        ServerScoreboard scoreboard = server.getScoreboard();

        if(scoreboard.containsObjective("modmdo.gots")) {
            config.set("ranking_object", rankingObject = "game.online.times");

            ((Scoreboard) scoreboard).setObjectiveSlot(1, scoreboard.getObjective("modmdo.gots"));
        } else {
            throw new IllegalStateException();
        }
    }

    public void addGameOnlineTimeScoreboard(MinecraftServer server, Text displayName) {
        ServerScoreboard scoreboard = server.getScoreboard();
        if(scoreboard.containsObjective("modmdo.gots")) {
            scoreboard.removeObjective(scoreboard.getObjective("modmdo.gots"));
        }
        scoreboard.addObjective("modmdo.gots", ScoreboardCriterion.DUMMY, displayName, ScoreboardCriterion.DUMMY.getDefaultRenderType());
    }

    public void showDestroys(MinecraftServer server) throws IllegalStateException {
        ServerScoreboard scoreboard = server.getScoreboard();

        if(scoreboard.containsObjective("modmdo.dsy")) {
            config.set("ranking_object", rankingObject = "destroy.blocks");

            ((Scoreboard) scoreboard).setObjectiveSlot(1, scoreboard.getObjective("modmdo.dsy"));
        } else {
            throw new IllegalStateException();
        }
    }

    public void addDestroysScoreboard(MinecraftServer server, Text displayName) {
        ServerScoreboard scoreboard = server.getScoreboard();
        if(scoreboard.containsObjective("modmdo.dsy")) {
            scoreboard.removeObjective(scoreboard.getObjective("modmdo.dsy"));
        }
        scoreboard.addObjective("modmdo.dsy", ScoreboardCriterion.DUMMY, displayName, ScoreboardCriterion.DUMMY.getDefaultRenderType());
    }

    public void showTrades(MinecraftServer server) throws IllegalStateException {
        ServerScoreboard scoreboard = server.getScoreboard();

        if(scoreboard.containsObjective("modmdo.trd")) {
            config.set("ranking_object", rankingObject = "villager.trades");

            ((Scoreboard) scoreboard).setObjectiveSlot(1, scoreboard.getObjective("modmdo.trd"));
        } else {
            throw new IllegalStateException();
        }
    }

    public void addTradesScoreboard(MinecraftServer server, Text displayName) {
        ServerScoreboard scoreboard = server.getScoreboard();
        if(scoreboard.containsObjective("modmdo.trd")) {
            scoreboard.removeObjective(scoreboard.getObjective("modmdo.trd"));
        }
        scoreboard.addObjective("modmdo.trd", ScoreboardCriterion.DUMMY, displayName, ScoreboardCriterion.DUMMY.getDefaultRenderType());
    }

    public void showDeaths(MinecraftServer server) throws IllegalStateException {
        ServerScoreboard scoreboard = server.getScoreboard();

        if(scoreboard.containsObjective("modmdo.dts")) {
            config.set("ranking_object", rankingObject = "player.deaths");

            ((Scoreboard) scoreboard).setObjectiveSlot(1, scoreboard.getObjective("modmdo.dts"));
        } else {
            throw new IllegalStateException();
        }
    }

    public void addDeathsScoreboard(MinecraftServer server, Text displayName) {
        ServerScoreboard scoreboard = server.getScoreboard();
        if(scoreboard.containsObjective("modmdo.dts")) {
            scoreboard.removeObjective(scoreboard.getObjective("modmdo.dts"));
        }
        scoreboard.addObjective("modmdo.dts", ScoreboardCriterion.DUMMY, displayName, ScoreboardCriterion.DUMMY.getDefaultRenderType());
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
    public void init() {
        LOGGER.info("initializing ranking object");

        statObjects.add("destroy.blocks");
        statObjects.add("villager.trades");
        statObjects.add("player.deaths");
        statObjects.add("game.online.times");

        String gameOnlineTimeScale = config.getConfigString("ranking_game_online_time_scale");
        String onlineTimeScale = config.getConfigString("ranking_online_time_scale");
        String ranking = config.getConfigString("ranking_object");

        if(ranking != null)
            rankingObject = ranking;

        if(onlineTimeScale != null)
            rankingOnlineTimeScale = onlineTimeScale;

        if(onlineTimeScale != null)
            rankingGameOnlineTimeScale = gameOnlineTimeScale;

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

        LOGGER.info("initialized ranking object");
    }
}
