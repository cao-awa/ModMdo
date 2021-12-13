package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.storage.Variables;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class ModMdoConfigCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("modmdo").then(literal("enableHere").executes(getHereReceive -> {
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

            dispatcher.register(literal("modmdo").then(literal("enableSecureEnchant").executes(getHereReceive -> {
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

//            dispatcher.register(literal("modmdo").then(literal("enableDeadFeedback").executes(getHereReceive -> {
//                ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
//                getHereReceive.getSource().sendFeedback(formatProfileReturnMessage( "receiveDeadMessage", getUserDeadMessageReceive(player.getUuid())), false);
//
//                return 2;
//            }).then(literal("enable").executes(receive -> {
//                ServerPlayerEntity player = receive.getSource().getPlayer();
//                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "receive");
//                receive.getSource().sendFeedback(receiveDeadMessage(), false);
//
//                return 1;
//            })).then(literal("disable").executes(rejection -> {
//                ServerPlayerEntity player = rejection.getSource().getPlayer();
//                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "rejection");
//                rejection.getSource().sendFeedback(rejectionDeadMessage(), false);
//
//                return 0;
//            }))));
//
//            dispatcher.register(literal("modmdo").then(literal("enableCava").executes(getHereReceive -> {
//                ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
//                getHereReceive.getSource().sendFeedback(formatConfigReturnMessage("cava"), false);
//
//                return 2;
//            }).then(literal("enable").executes(receive -> {
//                ServerPlayerEntity player = receive.getSource().getPlayer();
//                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "receive");
//                receive.getSource().sendFeedback(receiveDeadMessage(), false);
//
//                return 1;
//            })).then(literal("disable").executes(rejection -> {
//                ServerPlayerEntity player = rejection.getSource().getPlayer();
//                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "rejection");
//                rejection.getSource().sendFeedback(rejectionDeadMessage(), false);
//
//                return 0;
//            }))));
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
}
