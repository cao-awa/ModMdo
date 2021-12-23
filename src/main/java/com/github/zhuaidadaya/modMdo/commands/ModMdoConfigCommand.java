package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.login.token.EncryptionTokenUtil;
import com.github.zhuaidadaya.modMdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modMdo.storage.Variables;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModMdoConfigCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("modmdo").requires(level -> level.hasPermissionLevel(4)).then(literal("enableHere").executes(getHereReceive -> {
                getHereReceive.getSource().sendFeedback(formatConfigReturnMessage("here_command"), false);

                return 2;
            }).then(literal("enable").executes(receive -> {
                enableHereCommand = true;
                updateModMdoVariables();
                receive.getSource().sendFeedback(formatEnableHere(), false);

                return 1;
            })).then(literal("disable").executes(rejection -> {
                enableHereCommand = false;
                updateModMdoVariables();
                rejection.getSource().sendFeedback(formatDisableHere(), false);

                return 0;
            }))).then(literal("enableSecureEnchant").executes(getHereReceive -> {
                getHereReceive.getSource().sendFeedback(formatConfigReturnMessage("secure_enchant"), false);

                return 2;
            }).then(literal("enable").executes(receive -> {
                enableSecureEnchant = true;
                updateModMdoVariables();
                receive.getSource().sendFeedback(formatEnableSecureEnchant(), false);

                return 1;
            })).then(literal("disable").executes(rejection -> {
                enableSecureEnchant = false;
                updateModMdoVariables();
                rejection.getSource().sendFeedback(formatDisableSecureEnchant(), false);

                return 0;
            }))).then(literal("enableEncryptionToken").executes(getHereReceive -> {
                getHereReceive.getSource().sendFeedback(formatConfigReturnMessage("encryption_token"), false);

                return 2;
            }).then(literal("enable").executes(receive -> {
                enableEncryptionToken = true;
                updateModMdoVariables();

                if(config.getConfig("token_by_encryption") != null) {
                    initModMdoToken();
                } else {
                    try {
                        modMdoToken = new EncryptionTokenUtil(ServerEncryptionToken.createServerEncryptionToken());
                        LOGGER.info("spawned new encryption token, check the config file");
                    } catch (Exception e) {
                        enableEncryptionToken = false;
                        LOGGER.info("failed to enable encryption token");
                    }
                }

                receive.getSource().sendFeedback(formatEnableEncryptionToken(), false);

                return 1;
            })).then(literal("disable").executes(rejection -> {
                enableEncryptionToken = false;
                updateModMdoVariables();
                rejection.getSource().sendFeedback(formatDisableEncryptionToken(), false);

                return 0;
            }))).then(literal("enableRejectReconnect").executes(getHereReceive -> {
                getHereReceive.getSource().sendFeedback(formatConfigReturnMessage("reject_reconnect"), false);

                return 2;
            }).then(literal("enable").executes(receive -> {
                enableRejectReconnect = true;
                updateModMdoVariables();

                receive.getSource().sendFeedback(formatEnableRejectReconnect(), false);

                return 1;
            })).then(literal("disable").executes(rejection -> {
                enableRejectReconnect = false;
                updateModMdoVariables();
                rejection.getSource().sendFeedback(formatDisableRejectReconnect(), false);

                return 0;
            }))).then(literal("enableDeadMessage").executes(getHereReceive -> {
                getHereReceive.getSource().sendFeedback(formatConfigReturnMessage("dead_message"), false);

                return 2;
            }).then(literal("enable").executes(receive -> {
                enableRejectReconnect = true;
                updateModMdoVariables();

                receive.getSource().sendFeedback(formatEnableDeadMessage(), false);

                return 1;
            })).then(literal("disable").executes(rejection -> {
                enableRejectReconnect = false;
                updateModMdoVariables();
                rejection.getSource().sendFeedback(formatDisabledDeadMessage(), false);

                return 0;
            }))).then(literal("itemDespawnTicks").executes(getDespawnTicks -> {
                getDespawnTicks.getSource().sendFeedback(formatItemDespawnTicks(), false);

                return 2;
            }).then(literal("become").then(argument("ticks", IntegerArgumentType.integer()).executes(setTicks -> {
                itemDespawnAge = Integer.parseInt(setTicks.getInput().split(" ")[2]);

                setTicks.getSource().sendFeedback(formatItemDespawnTicks(), false);

                return 1;
            }))).then(literal("original").executes(setTicksToDefault -> {
                itemDespawnAge = 6000;

                setTicksToDefault.getSource().sendFeedback(formatItemDespawnTicks(), false);

                return 2;
            }))));
        });
    }

    public TranslatableText formatConfigReturnMessage(String config) {
        return new TranslatableText(config + "." + Variables.config.getConfigValue(config) + ".rule.format");
    }

    public TranslatableText formatItemDespawnTicks() {
        return new TranslatableText("item.despawn.ticks.rule.format", itemDespawnAge);
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
