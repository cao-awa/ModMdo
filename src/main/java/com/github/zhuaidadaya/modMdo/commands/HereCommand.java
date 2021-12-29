package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.simple.vec.XYZ;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static com.github.zhuaidadaya.modMdo.storage.Variables.commandApplyToPlayer;
import static net.minecraft.server.command.CommandManager.literal;

public class HereCommand extends SimpleCommandOperation implements SimpleCommand, HereCommandFormat {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("here").executes(here -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_HERE, getPlayer(here), this, here)) {
                    ServerCommandSource source = here.getSource();
                    if(enableHereCommand) {
                        try {
                            ServerPlayerEntity whoUseHere = source.getPlayer();
                            PlayerManager p = source.getServer().getPlayerManager();
                            XYZ xyz = new XYZ(whoUseHere.getX(), whoUseHere.getY(), whoUseHere.getZ());
                            String dimension = whoUseHere.getEntityWorld().getDimension().getEffects().getPath();
                            for(ServerPlayerEntity player : p.getPlayerList()) {
                                if(commandApplyToPlayer(MODMDO_COMMAND_HERE, getPlayer(here), this, here)) {
                                    if(isUserHereReceive(player.getUuid())) {
                                        TranslatableText hereMessage = formatHereTip(dimension, xyz, player, whoUseHere);
                                        player.sendMessage(hereMessage, false);
                                    }
                                }
                            }
                            whoUseHere.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(24), 400, 5), whoUseHere);
                            source.sendFeedback(formatHereFeedBack(whoUseHere), true);
                            return 1;
                        } catch (Exception e) {
                            try {
                                source.sendError(formatHereFailedFeedBack(source.getPlayer()));
                            } catch (CommandSyntaxException ex) {

                            }
                            return - 1;
                        }
                    } else {
                        source.sendError(formatHereDisabled());
                    }
                }
                return 0;
            }));
        });
    }

    @Override
    public TranslatableText formatHereDisabled() {
        return new TranslatableText("here_command.disable.rule.format");
    }

    @Override
    public TranslatableText formatHereTip(String dimension, XYZ xyz, ServerPlayerEntity player, ServerPlayerEntity whoUseHere) {
        String useHerePlayerName = whoUseHere.getName().asString();

        return new TranslatableText("command.here", useHerePlayerName,dimensionTips.getDimensionColor(dimension),useHerePlayerName,dimensionTips.getDimensionName(dimension),"§e" + xyz.getIntegerXYZ());
    }

    @Override
    public TranslatableText formatHereFeedBack(ServerPlayerEntity player) {
        String playerName = player.getName().asString();
        return new TranslatableText("command.here.feedback", playerName);
    }

    @Override
    public TranslatableText formatHereFailedFeedBack(ServerPlayerEntity player) {
        return new TranslatableText("command.here.failed.feedback");
    }
}
