package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.commands.argument.*;
import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.permission.PermissionLevel;
import com.github.zhuaidadaya.modmdo.storage.Variables;
import com.github.zhuaidadaya.modmdo.utils.command.SimpleCommandOperation;
import com.github.zhuaidadaya.modmdo.utils.translate.TranslateUtil;
import com.github.zhuaidadaya.modmdo.whitelist.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EnchantmentArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.server.command.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.util.Locale;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static com.github.zhuaidadaya.modmdo.storage.Variables.getLanguage;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModMdoConfigCommand extends SimpleCommandOperation implements SimpleCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("modmdo").requires(level -> level.hasPermissionLevel(4)).then(literal("here").executes(here -> {
                sendFeedback(here, formatConfigReturnMessage("here_command"), 1);
                return 2;
            }).then(literal("enable").executes(enableHere -> {
                enableHereCommand = true;
                updateModMdoVariables();
                sendFeedback(enableHere, formatEnableHere(), 1);
                return 1;
            })).then(literal("disable").executes(disableHere -> {
                enableHereCommand = false;
                updateModMdoVariables();
                sendFeedback(disableHere, formatDisableHere(), 1);
                return 0;
            }))).then(literal("secureEnchant").executes(secureEnchant -> {
                sendFeedback(secureEnchant, formatConfigReturnMessage("secure_enchant"), 1);
                return 2;
            }).then(literal("enable").executes(enableSecureEnchant -> {
                Variables.enableSecureEnchant = true;
                updateModMdoVariables();
                sendFeedback(enableSecureEnchant, formatEnableSecureEnchant(), 1);
                return 1;
            })).then(literal("disable").executes(disableSecureEnchant -> {
                enableSecureEnchant = false;
                updateModMdoVariables();
                sendFeedback(disableSecureEnchant, formatDisableSecureEnchant(), 1);
                return 0;
            }))).then(literal("useModMdoWhitelist").executes(whitelist -> {
                updateModMdoVariables();

                sendFeedback(whitelist, formatConfigReturnMessage("modmdo_whitelist"), 1);
                return 2;
            }).then(literal("enable").executes(enableWhitelist -> {
                config.set("modmdo_whitelist", true);
                updateModMdoVariables();

                sendFeedback(enableWhitelist, formatUseModMdoWhitelist(), 1);
                return 1;
            })).then(literal("disable").executes(disableWhitelist -> {
                config.set("modmdo_whitelist", false);
                updateModMdoVariables();
                sendFeedback(disableWhitelist, formatDisableModMdoWhitelist(), 1);
                return 0;
            }))).then(literal("rejectReconnect").executes(rejectReconnect -> {
                sendFeedback(rejectReconnect, formatConfigReturnMessage("reject_reconnect"), 1);
                return 2;
            }).then(literal("enable").executes(reject -> {
                if (commandApplyToPlayer(1, getPlayer(reject), this, reject)) {
                    enableRejectReconnect = true;
                    updateModMdoVariables();

                    sendFeedback(reject, formatEnableRejectReconnect(), 1);
                }
                return 1;
            })).then(literal("disable").executes(receive -> {
                enableRejectReconnect = false;
                updateModMdoVariables();
                sendFeedback(receive, formatDisableRejectReconnect(), 1);
                return 0;
            }))).then(literal("deadMessage").executes(deadMessage -> {
                sendFeedback(deadMessage, formatConfigReturnMessage("dead_message"), 1);
                return 2;
            }).then(literal("enable").executes(enabled -> {
                enableDeadMessage = true;
                updateModMdoVariables();

                sendFeedback(enabled, formatEnableDeadMessage(), 1);
                return 1;
            })).then(literal("disable").executes(disable -> {
                enableDeadMessage = false;
                updateModMdoVariables();
                sendFeedback(disable, formatDisabledDeadMessage(), 1);
                return 0;
            }))).then(literal("itemDespawnTicks").executes(getDespawnTicks -> {
                sendFeedback(getDespawnTicks, formatItemDespawnTicks(), 1);
                return 2;
            }).then(literal("become").then(argument("ticks", IntegerArgumentType.integer(- 1)).executes(setTicks -> {
                itemDespawnAge = IntegerArgumentType.getInteger(setTicks, "ticks");

                sendFeedbackAndInform(setTicks, formatItemDespawnTicks(), 1);
                return 1;
            }))).then(literal("original").executes(setTicksToDefault -> {
                itemDespawnAge = 6000;

                sendFeedbackAndInform(setTicksToDefault, formatItemDespawnTicks(), 1);
                return 2;
            }))).then(literal("tickingEntities").executes(getTickingEntities -> {
                sendFeedbackAndInform(getTickingEntities, formatTickingEntitiesTick(), 1);
                return 0;
            }).then(literal("enable").executes(enableTickingEntities -> {
                cancelEntitiesTick = false;

                sendFeedbackAndInform(enableTickingEntities, formatTickingEntitiesTick(), 1);
                return 1;
            })).then(literal("disable").executes(disableTickingEntities -> {
                cancelEntitiesTick = true;

                sendFeedbackAndInform(disableTickingEntities, formatTickingEntitiesTick(), 1);
                return 2;
            }))).then(literal("timeActive").executes(getTimeActive -> {
                sendFeedback(getTimeActive, formatConfigReturnMessage("time_active"), 15);
                return 0;
            }).then(literal("enable").executes(enableTimeActive -> {
                timeActive = true;

                updateModMdoVariables();

                sendFeedback(enableTimeActive, formatConfigReturnMessage("time_active"), 15);
                return 0;
            })).then(literal("disable").executes(disableTimeActive -> {
                timeActive = false;

                updateModMdoVariables();

                sendFeedback(disableTimeActive, formatConfigReturnMessage("time_active"), 15);
                return 0;
            }))).then(literal("loginCheckTimeLimit").executes(getTimeLimit -> {
                sendFeedback(getTimeLimit, formatCheckerTimeLimit(), 16);
                return 0;
            }).then(argument("ms", IntegerArgumentType.integer(500)).executes(setTimeLimit -> {
                config.set("checker_time_limit", IntegerArgumentType.getInteger(setTimeLimit, "ms"));

                updateModMdoVariables();

                sendFeedback(setTimeLimit, formatCheckerTimeLimit(), 16);
                return 0;
            }))).then(literal("language").executes(getLanguage -> {
                sendFeedback(getLanguage, new TranslatableText("language.default", getLanguage()), 20);
                return 0;
            }).then(literal("chinese").executes(chinese -> {
                config.set("default_language", Language.CHINESE);
                updateModMdoVariables();
                sendFeedback(chinese, new TranslatableText("language.default", getLanguage()), 20);
                return 0;
            })).then(literal("english").executes(english -> {
                config.set("default_language", Language.ENGLISH);
                updateModMdoVariables();
                sendFeedback(english, new TranslatableText("language.default", getLanguage()), 20);
                return 0;
            }))).then(literal("maxEnchantmentLevel").executes(getEnchantControlEnable -> {
                sendFeedback(getEnchantControlEnable, TranslateUtil.translatableText(enchantLevelController.isEnabledControl() ? "enchantment.level.controller.enabled" : "enchantment.level.controller.disabled"), 21);
                return 0;
            }).then(literal("enable").executes(enableEnchantLimit -> {
                enchantLevelController.setEnabledControl(true);
                saveEnchantmentMaxLevel();
                sendFeedback(enableEnchantLimit, TranslateUtil.translatableText(enchantLevelController.isEnabledControl() ? "enchantment.level.controller.enabled" : "enchantment.level.controller.disabled"), 21);
                return 0;
            })).then(literal("disable").executes(disableEnchantLimit -> {
                enchantLevelController.setEnabledControl(false);
                saveEnchantmentMaxLevel();
                sendFeedback(disableEnchantLimit, TranslateUtil.translatableText(enchantLevelController.isEnabledControl() ? "enchantment.level.controller.enabled" : "enchantment.level.controller.disabled"), 21);
                return 0;
            })).then(literal("limit").then(literal("all").then(argument("all", IntegerArgumentType.integer(0, Short.MAX_VALUE)).executes(setDef -> {
                short level = (short) IntegerArgumentType.getInteger(setDef, "all");
                enchantLevelController.setAll(level);
                sendFeedback(setDef, new TranslatableText("enchantment.max.level.limit.all", level), 21);
                return 0;
            })).then(literal("default").executes(recoveryAll -> {
                enchantLevelController.allDefault();
                sendFeedback(recoveryAll, new TranslatableText("enchantment.max.level.limit.all.default"), 21);
                return 0;
            }))).then(literal("appoint").then(argument("appoint", EnchantmentArgumentType.enchantment()).executes(getLimit -> {
                Identifier name = EnchantmentHelper.getEnchantmentId(EnchantmentArgumentType.getEnchantment(getLimit, "appoint"));
                short level = enchantLevelController.get(name).getMax();
                sendFeedback(getLimit, new TranslatableText("enchantment.max.level.limit", name, level), 21);
                saveEnchantmentMaxLevel();
                return 0;
            }).then(argument("limit", IntegerArgumentType.integer(0, Short.MAX_VALUE)).executes(setLimit -> {
                Identifier name = EnchantmentHelper.getEnchantmentId(EnchantmentArgumentType.getEnchantment(setLimit, "appoint"));
                short level = (short) IntegerArgumentType.getInteger(setLimit, "limit");
                enchantLevelController.set(name, level);
                saveEnchantmentMaxLevel();
                sendFeedback(setLimit, new TranslatableText("enchantment.max.level.limit", name, level), 21);
                return 0;
            })).then(literal("default").executes(recoveryLevel -> {
                Identifier name = EnchantmentHelper.getEnchantmentId(EnchantmentArgumentType.getEnchantment(recoveryLevel, "appoint"));
                short level = enchantLevelController.get(name).getDefaultMax();
                enchantLevelController.set(name, level);
                saveEnchantmentMaxLevel();
                sendFeedback(recoveryLevel, new TranslatableText("enchantment.max.level.limit", name, level), 21);
                return 0;
            })))))).then(literal("clearEnchantIfLevelTooHigh").executes(getClear -> {
                sendFeedback(getClear, TranslateUtil.formatRule("enchantment_clear_if_level_too_high", clearEnchantIfLevelTooHigh ? "enabled" : "disabled"), 21);
                return 0;
            }).then(literal("enable").executes(enableClear -> {
                clearEnchantIfLevelTooHigh = true;
                updateModMdoVariables();
                sendFeedback(enableClear, TranslateUtil.formatRule("enchantment_clear_if_level_too_high", "enabled"), 21);
                return 0;
            })).then(literal("disable").executes(disableClear -> {
                clearEnchantIfLevelTooHigh = false;
                updateModMdoVariables();
                sendFeedback(disableClear, TranslateUtil.formatRule("enchantment_clear_if_level_too_high", "disabled"), 21);
                return 0;
            }))).then(literal("rejectNoFallCheat").executes(getRejectNoFall -> {
                sendFeedback(getRejectNoFall, new TranslatableText(rejectNoFallCheat ? "player.no.fall.cheat.reject" : "player.no.fall.cheat.receive"), 21);
                return 0;
            }).then(literal("enable").executes(reject -> {
                rejectNoFallCheat = true;
                updateModMdoVariables();
                sendFeedback(reject, new TranslatableText(rejectNoFallCheat ? "player.no.fall.cheat.reject" : "player.no.fall.cheat.receive"), 21);
                return 0;
            })).then(literal("disable").executes(receive -> {
                rejectNoFallCheat = false;
                updateModMdoVariables();
                sendFeedback(receive, new TranslatableText(rejectNoFallCheat ? "player.no.fall.cheat.reject" : "player.no.fall.cheat.receive"), 21);
                return 0;
            }))).then(literal("onlyCheckIdentifier").executes(check -> {
                sendFeedback(check, formatConfigReturnMessage("whitelist_only_id"));
                return 0;
            }).then(literal("enable").executes(enable -> {
                config.set("whitelist_only_id", true);
                sendFeedback(enable, formatConfigReturnMessage("whitelist_only_id"));
                return 0;
            })).then(literal("disable").executes(disable -> {
                config.set("whitelist_only_id", false);
                sendFeedback(disable, formatConfigReturnMessage("whitelist_only_id"));
                return 0;
            }))).then(literal("whitelist").then(literal("remove").then(argument("name", ModMdoWhitelistArgumentType.whitelist()).executes(remove -> {
                Whitelist wl = ModMdoWhitelistArgumentType.getWhiteList(remove, "name");
                if (whitelist.containsName(wl.getName())) {
                    whitelist.remove(wl.getName());
                    sendFeedback(remove, new TranslatableText("modmdo.whitelist.removed", wl.getName()));
                    updateWhitelistNames(getServer(remove), true);
                    return 0;
                }
                sendError(remove, new TranslatableText("arguments.permanent.whitelist.not.registered"), 25);
                return - 1;
            }))).then(literal("list").executes(showWhiteList -> {
                showWhitelist(showWhiteList);
                return 0;
            }))).then(literal("compatibleOnlineMode").executes(getCompatible -> {
                sendFeedback(getCompatible, formatConfigReturnMessage("compatible_online_mode"));
                return 0;
            }).then(literal("enable").executes(enable -> {
                config.set("compatible_online_mode", true);
                sendFeedback(enable, formatConfigReturnMessage("compatible_online_mode"));
                return 0;
            })).then(literal("disable").executes(disable -> {
                config.set("compatible_online_mode", false);
                sendFeedback(disable, formatConfigReturnMessage("compatible_online_mode"));
                return 0;
            }))));
        });
    }

    public void showWhitelist(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        flushTemporaryWhitelist();
        ServerPlayerEntity player = getPlayer(source);
        if (whitelist.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (Whitelist wl : whitelist.values()) {
                builder.append(wl.getName()).append(", ");
            }
            builder.delete(builder.length() - 2, builder.length());
            sendMessage(player, new TranslatableText("commands.modmdo.whitelist.list", whitelist.size(), builder.toString()), false, 22);
        } else {
            sendMessage(player, new TranslatableText("commands.modmdo.whitelist.none"), false, 22);

        }
    }

    public TranslatableText formatConfigReturnMessage(String config) {
        return new TranslatableText(config + "." + Variables.config.getConfigString(config) + ".rule.format");
    }

    public TranslatableText formatCheckerTimeLimit() {
        return new TranslatableText("checker_time_limit.rule.format", config.getConfigInt("checker_time_limit"));
    }

    public TranslatableText formatTickingEntitiesTick() {
        return new TranslatableText(cancelEntitiesTick ? "ticking.entities.false.rule.format" : "ticking.entities.true.rule.format");
    }

    public TranslatableText formatItemDespawnTicks() {
        if (itemDespawnAge > - 1) {
            return new TranslatableText("item.despawn.ticks.rule.format", itemDespawnAge);
        } else {
            return new TranslatableText("item.despawn.ticks.false.rule.format", itemDespawnAge);
        }
    }

    public TranslatableText formatEnableHere() {
        return new TranslatableText("here_command.true.rule.format");
    }

    public TranslatableText formatDisableHere() {
        return new TranslatableText("here_command.false.rule.format");
    }

    public TranslatableText formatEnableSecureEnchant() {
        return new TranslatableText("secure_enchant.true.rule.format");
    }

    public TranslatableText formatDisableSecureEnchant() {
        return new TranslatableText("secure_enchant.false.rule.format");
    }

    public TranslatableText formatUseModMdoWhitelist() {
        return new TranslatableText("modmdo_whitelist.true.rule.format");
    }

    public TranslatableText formatDisableModMdoWhitelist() {
        return new TranslatableText("modmdo_whitelist.false.rule.format");
    }

    public TranslatableText formatEnableRejectReconnect() {
        return new TranslatableText("reject_reconnect.true.rule.format");
    }

    public TranslatableText formatDisableRejectReconnect() {
        return new TranslatableText("reject_reconnect.reject.false.rule.format");
    }

    public TranslatableText formatEnableDeadMessage() {
        return new TranslatableText("dead_message.true.rule.format");
    }

    public TranslatableText formatDisabledDeadMessage() {
        return new TranslatableText("dead_message.false.rule.format");
    }

    public TranslatableText formatJoinGameFollow() {
        return TranslateUtil.formatRule("follow.join.server", config.getConfigString("join_server_follow").toLowerCase(Locale.ROOT));
    }

    public TranslatableText formatRunCommandFollow() {
        return TranslateUtil.formatRule("follow.run.command", config.getConfigString("run_command_follow").toLowerCase(Locale.ROOT));
    }
}
