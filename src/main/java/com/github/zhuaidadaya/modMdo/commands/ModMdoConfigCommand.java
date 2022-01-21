package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.login.token.EncryptionTokenUtil;
import com.github.zhuaidadaya.modMdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modMdo.permission.PermissionLevel;
import com.github.zhuaidadaya.modMdo.storage.Variables;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.text.TranslatableText;

import java.util.Locale;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModMdoConfigCommand extends SimpleCommandOperation implements SimpleCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("modmdo").requires(level -> level.hasPermissionLevel(4)).then(literal("here").executes(here -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(here), this, here)) {
                    sendFeedback(here,formatConfigReturnMessage("here_command"));
                }
                return 2;
            }).then(literal("enable").executes(enableHere -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(enableHere), this, enableHere)) {
                    enableHereCommand = true;
                    updateModMdoVariables();
                    sendFeedback(enableHere,formatEnableHere());
                }
                return 1;
            })).then(literal("disable").executes(disableHere -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(disableHere), this, disableHere)) {
                    enableHereCommand = false;
                    updateModMdoVariables();
                    sendFeedback(disableHere,formatDisableHere());
                }
                return 0;
            }))).then(literal("secureEnchant").executes(secureEnchant -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(secureEnchant), this, secureEnchant)) {

                    sendFeedback(secureEnchant,formatConfigReturnMessage("secure_enchant"));
                }
                return 2;
            }).then(literal("enable").executes(enableSecureEnchant -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(enableSecureEnchant), this, enableSecureEnchant)) {
                    Variables.enableSecureEnchant = true;
                    updateModMdoVariables();
                    sendFeedback(enableSecureEnchant,formatEnableSecureEnchant());
                }
                return 1;
            })).then(literal("disable").executes(disableSecureEnchant -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(disableSecureEnchant), this, disableSecureEnchant)) {
                    enableSecureEnchant = false;
                    updateModMdoVariables();
                    sendFeedback(disableSecureEnchant,formatDisableSecureEnchant());
                }
                return 0;
            }))).then(literal("encryptionToken").executes(encryptionToken -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(encryptionToken), this, encryptionToken)) {

                    sendFeedback(encryptionToken, formatConfigReturnMessage("encryption_token"));
                }
                return 2;
            }).then(literal("enable").executes(enableEncryptionToken -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(enableEncryptionToken), this, enableEncryptionToken)) {
                    Variables.enableEncryptionToken = true;
                    updateModMdoVariables();

                    if(config.getConfig("token_by_encryption") != null) {
                        initModMdoToken();
                    } else {
                        try {
                            modMdoToken = new EncryptionTokenUtil(ServerEncryptionToken.createServerEncryptionToken());
                            LOGGER.info("spawned new encryption token, check the config file");
                        } catch (Exception e) {
                            Variables.enableEncryptionToken = false;
                            LOGGER.info("failed to enable encryption token");
                        }
                    }

                    sendFeedback(enableEncryptionToken, formatEnableEncryptionToken());
                }
                return 1;
            })).then(literal("disable").executes(disableEncryptionToken -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(disableEncryptionToken), this, disableEncryptionToken)) {
                    enableEncryptionToken = false;
                    updateModMdoVariables();
                    sendFeedback(disableEncryptionToken, formatDisableEncryptionToken());
                }
                return 0;
            }))).then(literal("rejectReconnect").executes(rejectReconnect -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(rejectReconnect), this, rejectReconnect)) {
                    sendFeedback(rejectReconnect, formatConfigReturnMessage("reject_reconnect"));
                }
                return 2;
            }).then(literal("enable").executes(reject -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(reject), this, reject)) {
                    enableRejectReconnect = true;
                    updateModMdoVariables();

                    sendFeedback(reject, formatEnableRejectReconnect());
                }
                return 1;
            })).then(literal("disable").executes(receive -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(receive), this, receive)) {
                    enableRejectReconnect = false;
                    updateModMdoVariables();
                    sendFeedback(receive, formatDisableRejectReconnect());
                } return 0;
            }))).then(literal("deadMessage").executes(deadMessage -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(deadMessage), this,deadMessage)) {
                    sendFeedback(deadMessage, formatConfigReturnMessage("dead_message"));
                }
                return 2;
            }).then(literal("enable").executes(enabled -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(enabled), this,enabled)) {
                    enableDeadMessage = true;
                    updateModMdoVariables();

                    sendFeedback(enabled, formatEnableDeadMessage());
                }
                return 1;
            })).then(literal("disable").executes(disable -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(disable), this, disable)) {

                    enableDeadMessage = false;
                    updateModMdoVariables();
                    sendFeedback(disable, formatDisabledDeadMessage());
                }
                return 0;
            }))).then(literal("itemDespawnTicks").executes(getDespawnTicks -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(getDespawnTicks), this, getDespawnTicks)) {
                    sendFeedback(getDespawnTicks, formatItemDespawnTicks());
                }
                return 2;
            }).then(literal("become").then(argument("ticks", IntegerArgumentType.integer(-1)).executes(setTicks -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(setTicks), this, setTicks)) {
                    itemDespawnAge = Integer.parseInt(setTicks.getInput().split(" ")[3]);

                    sendFeedbackAndInform(setTicks, formatItemDespawnTicks());
                }
                return 1;
            }))).then(literal("original").executes(setTicksToDefault -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(setTicksToDefault), this, setTicksToDefault)) {
                    itemDespawnAge = 6000;

                    sendFeedbackAndInform(setTicksToDefault, formatItemDespawnTicks());
                }
                return 2;
            }))).then(literal("tickingEntities").executes(getTickingEntities -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(getTickingEntities), this, getTickingEntities)) {
                    sendFeedbackAndInform(getTickingEntities, formatTickingEntitiesTick());
                }
                return 0;
            }).then(literal("enable").executes(enableTickingEntities -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(enableTickingEntities), this, enableTickingEntities)) {
                    enabledCancelEntitiesTIck = false;

                    sendFeedbackAndInform(enableTickingEntities, formatTickingEntitiesTick());
                }
                return 1;
            })).then(literal("disable").executes(disableTickingEntities -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(disableTickingEntities), this, disableTickingEntities)) {
                    enabledCancelEntitiesTIck = true;

                    sendFeedbackAndInform(disableTickingEntities, formatTickingEntitiesTick());
                }
                return 2;
            }))).then(literal("tickingGame").executes(getTickingGame -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(getTickingGame), this, getTickingGame)) {
                    sendFeedbackAndInform(getTickingGame, formatTickingGame());
                }
                return 0;
            }).then(literal("enable").executes(enableTickingGame -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(enableTickingGame), this, enableTickingGame)) {
                    enabledCancelTIck = false;

                    sendFeedbackAndInform(enableTickingGame, formatTickingGame());
                }
                return 1;
            })).then(literal("disable").executes(disableTickingGame -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CONF, getPlayer(disableTickingGame), this, disableTickingGame)) {
                    enabledCancelTIck = true;

                    cancelTickStart = System.currentTimeMillis();
                    sendFeedbackAndInform(disableTickingGame, formatTickingGame());
                }
                return 2;
            }))).then(literal("joinServerFollow").executes(getJoinServerFollowLimit -> {
                if(config.getConfigString("joinServer") == null)
                    config.set("joinServer", PermissionLevel.OPS);

                sendFeedback(getJoinServerFollowLimit, formatJoinGameFollow());

                return 0;
            }).then(literal("disable").executes(disableJoinServerFollow -> {
                config.set("joinServer", PermissionLevel.UNABLE);

                sendFeedback(disableJoinServerFollow,formatJoinGameFollow());

                return 1;
            })).then(literal("all").executes(enableJoinServerFollowForAll -> {
                config.set("joinServer", PermissionLevel.ALL);

                sendFeedback(enableJoinServerFollowForAll,formatJoinGameFollow());

                return 2;
            })).then(literal("ops").executes(enableJoinServerFollowForOps -> {
                config.set("joinServer", PermissionLevel.OPS);

                sendFeedback(enableJoinServerFollowForOps, formatJoinGameFollow());

                return 3;
            }))).then(literal("runCommandFollow").executes(getJoinServerFollowLimit -> {
                if(config.getConfigString("runCommand") == null)
                    config.set("runCommand", PermissionLevel.OPS);

                sendFeedback(getJoinServerFollowLimit, formatRunCommandFollow());

                return 0;
            }).then(literal("disable").executes(disableJoinServerFollow -> {
                config.set("runCommand", PermissionLevel.UNABLE);

                sendFeedback(disableJoinServerFollow,formatRunCommandFollow());

                return 1;
            })).then(literal("all").executes(enableJoinServerFollowForAll -> {
                config.set("runCommand", PermissionLevel.ALL);

                sendFeedback(enableJoinServerFollowForAll,formatRunCommandFollow());

                return 2;
            })).then(literal("ops").executes(enableJoinServerFollowForOps -> {
                config.set("runCommand", PermissionLevel.OPS);

                sendFeedback(enableJoinServerFollowForOps, formatRunCommandFollow());

                return 3;
            }))));
        });
    }

    public TranslatableText formatConfigReturnMessage(String config) {
        return new TranslatableText(config + "." + Variables.config.getConfigString(config) + ".rule.format");
    }

    public TranslatableText formatConfigReturnMessage(String head,String info) {
        return new TranslatableText(head + "." + info + ".rule.format");
    }

    public TranslatableText formatJoinGameFollow() {
        return formatConfigReturnMessage("follow.join.server", config.getConfigString("joinServer").toLowerCase(Locale.ROOT));
    }

    public TranslatableText formatRunCommandFollow() {
        return formatConfigReturnMessage("follow.run.command", config.getConfigString("runCommand").toLowerCase(Locale.ROOT));
    }

    public TranslatableText formatTickingGame() {
        return new TranslatableText(enabledCancelTIck ? "ticking.server.disable.rule.format" : "ticking.server.enable.rule.format");
    }

    public TranslatableText formatTickingEntitiesTick() {
        return new TranslatableText(enabledCancelEntitiesTIck ? "ticking.entities.disable.rule.format" : "ticking.entities.enable.rule.format");
    }

    public TranslatableText formatItemDespawnTicks() {
        if(itemDespawnAge > -1) {
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
        return new TranslatableText("encryption_token.disable.rule.format");
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
