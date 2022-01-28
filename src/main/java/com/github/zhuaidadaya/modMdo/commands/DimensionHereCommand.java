package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.simple.vec.XYZ;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class DimensionHereCommand extends SimpleCommandOperation implements SimpleCommand, HereCommandFormat {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("dhere").executes(dhere -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_HERE, getPlayer(dhere), this, dhere)) {

                    ServerCommandSource source = dhere.getSource();
                    if(enableHereCommand) {
                        try {
                            ServerPlayerEntity whoUseHere = source.getPlayer();
                            PlayerManager p = source.getServer().getPlayerManager();
                            XYZ xyz = new XYZ(whoUseHere.getX(), whoUseHere.getY(), whoUseHere.getZ());
                            String dimension = dimensionTips.getDimension(whoUseHere);
                            for(ServerPlayerEntity player : p.getPlayerList()) {
                                if(commandApplyToPlayer(MODMDO_COMMAND_HERE, player, this, dhere)) {
                                    if(isUserHereReceive(player.getUuid())) {
                                        TranslatableText hereMessage = formatHereTip(dimension, xyz, player, whoUseHere);
                                        player.sendMessage(hereMessage, false);
                                    }
                                }
                            }
                            whoUseHere.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(24), 400, 5), whoUseHere);
                            source.sendFeedback(formatHereFeedBack(source.getPlayer()), true);
                            return 1;
                        } catch (Exception e) {
                            try {
                                source.sendError(formatHereFailedFeedBack(source.getPlayer()));
                            } catch (Exception ex) {

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
        String convertTarget = "";
        switch(dimension) {
            case "overworld" -> convertTarget = "the_nether";
            case "the_nether" -> convertTarget = "overworld";
            case "the_end" -> throw new IllegalArgumentException();
        }
        XYZ convertXYZ = xyz.clone();
        if(convertTarget.equals("the_nether")) {
            convertXYZ.divideXZ(8, 8);
        } else {
            convertXYZ.multiplyXZ(8, 8);
        }

        return new TranslatableText("command.dhere",  useHerePlayerName,"", dimensionTips.getDimensionColor(dimension) + useHerePlayerName, dimensionTips.getDimensionName(dimension), "§e" + xyz.getIntegerXYZ(), dimensionTips.getDimensionName(convertTarget), "§d" + convertXYZ.getIntegerXYZ());
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
