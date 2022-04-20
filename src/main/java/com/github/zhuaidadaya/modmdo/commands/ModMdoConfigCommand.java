package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.login.token.EncryptionTokenUtil;
import com.github.zhuaidadaya.modmdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modmdo.permission.PermissionLevel;
import com.github.zhuaidadaya.modmdo.storage.Variables;
import com.github.zhuaidadaya.modmdo.utils.command.SimpleCommandOperation;
import com.github.zhuaidadaya.modmdo.utils.translate.TranslateUtil;
import com.github.zhuaidadaya.rikaishinikui.handler.entrust.EntrustExecution;
import com.github.zhuaidadaya.rikaishinikui.handler.config.ObjectConfigUtil;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EnchantmentArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.json.JSONObject;

import java.util.Locale;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
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
                if (commandApplyToPlayer(1, getPlayer(disableHere), this, disableHere)) {
                    enableHereCommand = false;
                    updateModMdoVariables();
                    sendFeedback(disableHere, formatDisableHere());
                }
                return 0;
            }))).then(literal("secureEnchant").executes(secureEnchant -> {
                if (commandApplyToPlayer(1, getPlayer(secureEnchant), this, secureEnchant)) {

                    sendFeedback(secureEnchant, formatConfigReturnMessage("secure_enchant"));
                }
                return 2;
            }).then(literal("enable").executes(enableSecureEnchant -> {
                if (commandApplyToPlayer(1, getPlayer(enableSecureEnchant), this, enableSecureEnchant)) {
                    Variables.enableSecureEnchant = true;
                    updateModMdoVariables();
                    sendFeedback(enableSecureEnchant, formatEnableSecureEnchant());
                }
                return 1;
            })).then(literal("disable").executes(disableSecureEnchant -> {
                if (commandApplyToPlayer(1, getPlayer(disableSecureEnchant), this, disableSecureEnchant)) {
                    enableSecureEnchant = false;
                    updateModMdoVariables();
                    sendFeedback(disableSecureEnchant, formatDisableSecureEnchant());
                }
                return 0;
            }))).then(literal("encryptionToken").executes(encryptionToken -> {
                if (commandApplyToPlayer(1, getPlayer(encryptionToken), this, encryptionToken)) {

                    sendFeedback(encryptionToken, formatConfigReturnMessage("encryption_token"));
                }
                return 2;
            }).then(literal("enable").executes(enableEncryptionToken -> {
                if (commandApplyToPlayer(1, getPlayer(enableEncryptionToken), this, enableEncryptionToken)) {
                    Variables.enableEncryptionToken = true;
                    updateModMdoVariables();

                    try {
                        initModMdoToken();
                        modMdoToken.getServerToken().getToken();
                    } catch (NullPointerException npe) {
                        try {
                            modMdoToken = new EncryptionTokenUtil(ServerEncryptionToken.createServerEncryptionToken());
                            LOGGER.info("spawned new encryption token, check the config file");
                        } catch (Exception e) {
                            Variables.enableEncryptionToken = false;
                            LOGGER.info("failed to enable encryption token");
                        }
                    }

                    saveToken();

                    updateModMdoVariables();

                    tokenChanged = true;

                    sendFeedback(enableEncryptionToken, formatEnableEncryptionToken());
                }
                return 1;
            })).then(literal("disable").executes(disableEncryptionToken -> {
                if (commandApplyToPlayer(1, getPlayer(disableEncryptionToken), this, disableEncryptionToken)) {
                    enableEncryptionToken = false;
                    updateModMdoVariables();
                    sendFeedback(disableEncryptionToken, formatDisableEncryptionToken());
                }
                return 0;
            }))).then(literal("rejectReconnect").executes(rejectReconnect -> {
                if (commandApplyToPlayer(1, getPlayer(rejectReconnect), this, rejectReconnect)) {
                    sendFeedback(rejectReconnect, formatConfigReturnMessage("reject_reconnect"));
                }
                return 2;
            }).then(literal("enable").executes(reject -> {
                if (commandApplyToPlayer(1, getPlayer(reject), this, reject)) {
                    enableRejectReconnect = true;
                    updateModMdoVariables();

                    sendFeedback(reject, formatEnableRejectReconnect());
                }
                return 1;
            })).then(literal("disable").executes(receive -> {
                if (commandApplyToPlayer(1, getPlayer(receive), this, receive)) {
                    enableRejectReconnect = false;
                    updateModMdoVariables();
                    sendFeedback(receive, formatDisableRejectReconnect());
                }
                return 0;
            }))).then(literal("deadMessage").executes(deadMessage -> {
                if (commandApplyToPlayer(1, getPlayer(deadMessage), this, deadMessage)) {
                    sendFeedback(deadMessage, formatConfigReturnMessage("dead_message"));
                }
                return 2;
            }).then(literal("enable").executes(enabled -> {
                if (commandApplyToPlayer(1, getPlayer(enabled), this, enabled)) {
                    enableDeadMessage = true;
                    updateModMdoVariables();

                    sendFeedback(enabled, formatEnableDeadMessage());
                }
                return 1;
            })).then(literal("disable").executes(disable -> {
                if (commandApplyToPlayer(1, getPlayer(disable), this, disable)) {

                    enableDeadMessage = false;
                    updateModMdoVariables();
                    sendFeedback(disable, formatDisabledDeadMessage());
                }
                return 0;
            }))).then(literal("itemDespawnTicks").executes(getDespawnTicks -> {
                if (commandApplyToPlayer(1, getPlayer(getDespawnTicks), this, getDespawnTicks)) {
                    sendFeedback(getDespawnTicks, formatItemDespawnTicks());
                }
                return 2;
            }).then(literal("become").then(argument("ticks", IntegerArgumentType.integer(- 1)).executes(setTicks -> {
                if (commandApplyToPlayer(1, getPlayer(setTicks), this, setTicks)) {
                    itemDespawnAge = Integer.parseInt(setTicks.getInput().split(" ")[3]);

                    sendFeedbackAndInform(setTicks, formatItemDespawnTicks());
                }
                return 1;
            }))).then(literal("original").executes(setTicksToDefault -> {
                if (commandApplyToPlayer(1, getPlayer(setTicksToDefault), this, setTicksToDefault)) {
                    itemDespawnAge = 6000;

                    sendFeedbackAndInform(setTicksToDefault, formatItemDespawnTicks());
                }
                return 2;
            }))).then(literal("tickingEntities").executes(getTickingEntities -> {
                if (commandApplyToPlayer(1, getPlayer(getTickingEntities), this, getTickingEntities)) {
                    sendFeedbackAndInform(getTickingEntities, formatTickingEntitiesTick());
                }
                return 0;
            }).then(literal("enable").executes(enableTickingEntities -> {
                if (commandApplyToPlayer(1, getPlayer(enableTickingEntities), this, enableTickingEntities)) {
                    cancelEntitiesTick = false;

                    sendFeedbackAndInform(enableTickingEntities, formatTickingEntitiesTick());
                }
                return 1;
            })).then(literal("disable").executes(disableTickingEntities -> {
                if (commandApplyToPlayer(1, getPlayer(disableTickingEntities), this, disableTickingEntities)) {
                    cancelEntitiesTick = true;

                    sendFeedbackAndInform(disableTickingEntities, formatTickingEntitiesTick());
                }
                return 2;
            }))).then(literal("joinServerFollow").executes(getJoinServerFollowLimit -> {
                if (commandApplyToPlayer(10, getPlayer(getJoinServerFollowLimit), this, getJoinServerFollowLimit)) {
                    sendFeedback(getJoinServerFollowLimit, formatJoinGameFollow());
                }
                return 0;
            }).then(literal("disable").executes(disableJoinServerFollow -> {
                if (commandApplyToPlayer(10, getPlayer(disableJoinServerFollow), this, disableJoinServerFollow)) {
                    config.set("join_server_follow", PermissionLevel.UNABLE);

                    sendFeedback(disableJoinServerFollow, formatJoinGameFollow());
                }
                return 1;
            })).then(literal("all").executes(enableJoinServerFollowForAll -> {
                if (commandApplyToPlayer(10, getPlayer(enableJoinServerFollowForAll), this, enableJoinServerFollowForAll)) {
                    config.set("join_server_follow", PermissionLevel.ALL);

                    sendFeedback(enableJoinServerFollowForAll, formatJoinGameFollow());
                }
                return 2;
            })).then(literal("ops").executes(enableJoinServerFollowForOps -> {
                if (commandApplyToPlayer(10, getPlayer(enableJoinServerFollowForOps), this, enableJoinServerFollowForOps)) {
                    config.set("join_server_follow", PermissionLevel.OPS);

                    sendFeedback(enableJoinServerFollowForOps, formatJoinGameFollow());
                }
                return 3;
            }))).then(literal("runCommandFollow").executes(getJoinServerFollowLimit -> {
                if (commandApplyToPlayer(10, getPlayer(getJoinServerFollowLimit), this, getJoinServerFollowLimit)) {
                    sendFeedback(getJoinServerFollowLimit, formatRunCommandFollow());
                }
                return 0;
            }).then(literal("disable").executes(disableJoinServerFollow -> {
                if (commandApplyToPlayer(10, getPlayer(disableJoinServerFollow), this, disableJoinServerFollow)) {
                    config.set("run_command_follow", PermissionLevel.UNABLE);

                    sendFeedback(disableJoinServerFollow, formatRunCommandFollow());
                }
                return 1;
            })).then(literal("all").executes(enableJoinServerFollowForAll -> {
                if (commandApplyToPlayer(10, getPlayer(enableJoinServerFollowForAll), this, enableJoinServerFollowForAll)) {
                    config.set("run_command_follow", PermissionLevel.ALL);

                    sendFeedback(enableJoinServerFollowForAll, formatRunCommandFollow());
                }
                return 2;
            })).then(literal("ops").executes(enableJoinServerFollowForOps -> {
                if (commandApplyToPlayer(10, getPlayer(enableJoinServerFollowForOps), this, enableJoinServerFollowForOps)) {
                    config.set("run_command_follow", PermissionLevel.OPS);

                    sendFeedback(enableJoinServerFollowForOps, formatRunCommandFollow());
                }
                return 3;
            }))).then(literal("checkTokenPerTick").executes(getCheckTokenPerTick -> {
                if (commandApplyToPlayer(12, getPlayer(getCheckTokenPerTick), this, getCheckTokenPerTick)) {
                    sendFeedback(getCheckTokenPerTick, formatConfigReturnMessage("check_token_per_tick"));
                }
                return 0;
            }).then(literal("enable").executes(check -> {
                if (commandApplyToPlayer(12, getPlayer(check), this, check)) {
                    enableCheckTokenPerTick = true;

                    updateModMdoVariables();

                    sendFeedback(check, formatConfigReturnMessage("check_token_per_tick"));
                }
                return 0;
            })).then(literal("disable").executes(noCheck -> {
                if (commandApplyToPlayer(12, getPlayer(noCheck), this, noCheck)) {
                    enableCheckTokenPerTick = false;

                    updateModMdoVariables();

                    sendFeedback(noCheck, formatConfigReturnMessage("check_token_per_tick"));
                }
                return 0;
            }))).then(literal("timeActive").executes(getTimeActive -> {
                if (commandApplyToPlayer(15, getPlayer(getTimeActive), this, getTimeActive)) {
                    sendFeedback(getTimeActive, formatConfigReturnMessage("time_active"));
                }
                return 0;
            }).then(literal("enable").executes(enableTimeActive -> {
                if (commandApplyToPlayer(15, getPlayer(enableTimeActive), this, enableTimeActive)) {
                    timeActive = true;

                    updateModMdoVariables();

                    sendFeedback(enableTimeActive, formatConfigReturnMessage("time_active"));
                }
                return 0;
            })).then(literal("disable").executes(disableTimeActive -> {
                if (commandApplyToPlayer(15, getPlayer(disableTimeActive), this, disableTimeActive)) {
                    timeActive = false;

                    updateModMdoVariables();

                    sendFeedback(disableTimeActive, formatConfigReturnMessage("time_active"));
                }
                return 0;
            }))).then(literal("tokenCheckTimeLimit").executes(getTimeLimit -> {
                if (commandApplyToPlayer(16, getPlayer(getTimeLimit), this, getTimeLimit)) {
                    sendFeedback(getTimeLimit, formatCheckerTimeLimit());
                }
                return 0;
            }).then(argument("ms", IntegerArgumentType.integer(500)).executes(setTimeLimit -> {
                if (commandApplyToPlayer(16, getPlayer(setTimeLimit), this, setTimeLimit)) {
                    tokenCheckTimeLimit = IntegerArgumentType.getInteger(setTimeLimit, "ms");

                    updateModMdoVariables();

                    sendFeedback(setTimeLimit, formatCheckerTimeLimit());
                }
                return 0;
            }))).then(literal("language").executes(getLanguage -> {
                sendFeedback(getLanguage, new TranslatableText("language.default", language), 20);
                return 0;
            }).then(literal("chinese").executes(chinese -> {
                language = Language.CHINESE;
                updateModMdoVariables();
                sendFeedback(chinese, new TranslatableText("language.default", language), 20);
                return 0;
            })).then(literal("english").executes(english -> {
                language = Language.ENGLISH;
                updateModMdoVariables();
                sendFeedback(english, new TranslatableText("language.default", language), 20);
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
            }))).then(literal("registerPlayerUuid").then(literal("ops").executes(registerOps -> {
                registerPlayerUuid = PermissionLevel.OPS;
                updateModMdoVariables();
                EntrustExecution.before(config, first -> {
                    for (ServerPlayerEntity player : getServer(registerOps).getPlayerManager().getPlayerList()) {
                        playerCached.put(player.getName().asString(), new JSONObject().put("uuid", player.getUuid().toString()).put("name", player.getName().asString()));
                    }
                    updateModMdoVariables();
                }, ObjectConfigUtil::save);
                return 0;
            })).then(literal("all").executes(registerAll -> {
                registerPlayerUuid = PermissionLevel.ALL;
                updateModMdoVariables();
                EntrustExecution.before(config, first -> {
                    for (ServerPlayerEntity player : getServer(registerAll).getPlayerManager().getPlayerList()) {
                        playerCached.put(player.getName().asString(), new JSONObject().put("uuid", player.getUuid().toString()).put("name", player.getName().asString()));
                    }
                    updateModMdoVariables();
                }, ObjectConfigUtil::save);
                return 0;
            })).then(literal("disable").executes(disableRegister -> {
                registerPlayerUuid = PermissionLevel.UNABLE;
                updateModMdoVariables();
                return 0;
            }))).then(literal("registerPlayerNameRegex").executes(e -> {
                return 0;
            })));
        });
    }

    public TranslatableText formatConfigReturnMessage(String config) {
        return new TranslatableText(config + "." + Variables.config.getConfigString(config) + ".rule.format");
    }

    public TranslatableText formatCheckerTimeLimit() {
        return new TranslatableText("checker_time_limit.rule.format", tokenCheckTimeLimit);
    }

    public TranslatableText formatJoinGameFollow() {
        return TranslateUtil.formatRule("follow.join.server", config.getConfigString("join_server_follow").toLowerCase(Locale.ROOT));
    }

    public TranslatableText formatRunCommandFollow() {
        return TranslateUtil.formatRule("follow.run.command", config.getConfigString("run_command_follow").toLowerCase(Locale.ROOT));
    }

    public TranslatableText formatTickingEntitiesTick() {
        return new TranslatableText(cancelEntitiesTick ? "ticking.entities.disable.rule.format" : "ticking.entities.enable.rule.format");
    }

    public TranslatableText formatItemDespawnTicks() {
        if (itemDespawnAge > - 1) {
            return new TranslatableText("item.despawn.ticks.rule.format", itemDespawnAge);
        } else {
            return new TranslatableText("item.despawn.ticks.disable.rule.format", itemDespawnAge);
        }
    }

    public TranslatableText formatEnableHere() {
        return new TranslatableText("here_command.enable.rule.format");
    }

    public TranslatableText formatDisableHere() {
        return new TranslatableText("here_command.disable.rule.format");
    }

    public TranslatableText formatEnableSecureEnchant() {
        return new TranslatableText("secure_enchant.enable.rule.format");
    }

    public TranslatableText formatDisableSecureEnchant() {
        return new TranslatableText("secure_enchant.disable.rule.format");
    }

    public TranslatableText formatEnableEncryptionToken() {
        return new TranslatableText("encryption_token.enable.rule.format");
    }


    public TranslatableText formatDisableEncryptionToken() {
        return new TranslatableText("encryption_token.disable.rule.format");
    }

    public TranslatableText formatEnableRejectReconnect() {
        return new TranslatableText("reject_reconnect.enable.rule.format");
    }


    public TranslatableText formatDisableRejectReconnect() {
        return new TranslatableText("reject_reconnect.reject.disable.rule.format");
    }

    public TranslatableText formatEnableDeadMessage() {
        return new TranslatableText("dead_message.enable.rule.format");
    }


    public TranslatableText formatDisabledDeadMessage() {
        return new TranslatableText("dead_message.disable.rule.format");
    }
}
