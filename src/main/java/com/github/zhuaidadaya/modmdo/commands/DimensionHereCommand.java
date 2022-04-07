package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.simple.vec.XYZ;
import com.github.zhuaidadaya.modmdo.utils.command.SimpleCommandOperation;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class DimensionHereCommand extends SimpleCommandOperation implements SimpleCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("dhere").executes(dhere -> {
                ServerCommandSource source = dhere.getSource();
                if (enableHereCommand) {
                    try {
                        ServerPlayerEntity whoUseHere = source.getPlayer();
                        PlayerManager p = source.getServer().getPlayerManager();
                        XYZ xyz = new XYZ(whoUseHere.getX(), whoUseHere.getY(), whoUseHere.getZ());
                        String dimension = dimensionUtil.getDimension(whoUseHere);
                        for (ServerPlayerEntity player : p.getPlayerList()) {
                            if (isUserHereReceive(player.getUuid())) {
                                TranslatableText hereMessage = formatHereTip(dimension, xyz, whoUseHere);
                                sendMessage(player, hereMessage, false, 1);
                            }
                        }
                        whoUseHere.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(24), 400, 5), whoUseHere);
                        sendFeedback(source, formatHereFeedBack(source.getPlayer()), 1);
                        return 1;
                    } catch (Exception e) {
                        sendError(source, formatHereFailedFeedBack(), 1);

                        return - 1;
                    }
                } else {
                    sendError(source, formatHereDisabled(), 1);
                }
                return 0;
            }));
        });
    }

    public TranslatableText formatHereDisabled() {
        return new TranslatableText("here_command.disable.rule.format");
    }

    public TranslatableText formatHereTip(String dimension, XYZ xyz, ServerPlayerEntity whoUseHere) {
        String useHerePlayerName = whoUseHere.getName().asString();
        String convertTarget = "";
        switch (dimension) {
            case "overworld" -> convertTarget = "the_nether";
            case "the_nether" -> convertTarget = "overworld";
            case "the_end" -> throw new IllegalArgumentException();
        }
        XYZ convertXYZ = xyz.clone();
        if (convertTarget.equals("the_nether")) {
            convertXYZ.divideXZ(8, 8);
        } else {
            convertXYZ.multiplyXZ(8, 8);
        }

        return new TranslatableText("command.dhere", useHerePlayerName, "", dimensionUtil.getDimensionColor(dimension) + useHerePlayerName, dimensionUtil.getDimensionName(dimension), "§e" + xyz.getIntegerXYZ(), dimensionUtil.getDimensionName(convertTarget), "§d" + convertXYZ.getIntegerXYZ());
    }

    public TranslatableText formatHereFeedBack(ServerPlayerEntity player) {
        String playerName = player.getName().asString();
        return new TranslatableText("command.here.feedback", playerName);
    }

    public TranslatableText formatHereFailedFeedBack() {
        return new TranslatableText("command.here.failed.feedback");
    }
}
