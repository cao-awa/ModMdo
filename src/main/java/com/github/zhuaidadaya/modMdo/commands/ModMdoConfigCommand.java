package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.storage.Variables;
import com.github.zhuaidadaya.modMdo.test.AES;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
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
            }))));

            dispatcher.register(literal("modmdo").requires(level -> level.hasPermissionLevel(4)).then(literal("enableSecureEnchant").executes(getHereReceive -> {
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
            }))));

            dispatcher.register(literal("modmdo").requires(level -> level.hasPermissionLevel(4)).then(literal("enableEncryptionToken").executes(getHereReceive -> {
                getHereReceive.getSource().sendFeedback(formatConfigReturnMessage("encryption_token"), false);

                return 2;
            }).then(literal("enable").executes(receive -> {
                enableEncryptionToken = true;
                updateModMdoVariables();

                if(config.getConfig("token_by_encryption") != null) {
                    modMdoServerToken = new JSONObject(config.getConfigValue("token_by_encryption"));
                } else {
                    try {
                        modMdoServerToken = new JSONObject().put("server", new JSONObject().put("default", new AES().randomGet(128)).put("ops", new AES().randomGet(128)));
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
            }))));

            dispatcher.register(literal("modmdo").requires(level -> level.hasPermissionLevel(4)).then(literal("enableRejectReconnect").executes(getHereReceive -> {
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
            }))));

            dispatcher.register(literal("modmdo").requires(level -> level.hasPermissionLevel(4)).then(literal("enableDeadMessage").executes(getHereReceive -> {
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
            }))));
        });
    }

    public TranslatableText formatConfigReturnMessage(String config) {
        return new TranslatableText(config + "." + Variables.config.getConfigValue(config) + ".rule.format");
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
        return new TranslatableText("encryption.token.disable.rule.format");
    }


    public TranslatableText formatDisableEncryptionToken() {
        return new TranslatableText("encryption.token.disable.rule.format");
    }

    public TranslatableText formatEnableRejectReconnect() {
        return new TranslatableText("reconnect.reject.enable.rule.format");
    }


    public TranslatableText formatDisableRejectReconnect() {
        return new TranslatableText("reconnect.reject.disable.rule.format");
    }

    public TranslatableText formatEnableDeadMessage() {
        return new TranslatableText("dead.message.enable.rule.format");
    }


    public TranslatableText formatDisabledDeadMessage() {
        return new TranslatableText("dead.message.disable.rule.format");
    }
}
