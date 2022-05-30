package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.simple.vec.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.dimension.*;
import net.minecraft.entity.effect.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import static net.minecraft.server.command.CommandManager.*;

public class DimensionHereCommand extends SimpleCommand {
    public DimensionHereCommand register() {
        SharedVariables.commandRegister.register(literal("dhere").executes(dhere -> {
            ServerCommandSource source = dhere.getSource();
            if (SharedVariables.enableHereCommand) {
                try {
                    ServerPlayerEntity whoUseHere = source.getPlayer();
                    PlayerManager p = source.getServer().getPlayerManager();
                    XYZ xyz = new XYZ(whoUseHere.getX(), whoUseHere.getY(), whoUseHere.getZ());
                    String dimension = DimensionUtil.getDimension(whoUseHere);
                    for (ServerPlayerEntity player : p.getPlayerList()) {
                        TranslatableText hereMessage = formatHereTip(dimension, xyz, whoUseHere);
                        sendMessage(player, hereMessage, false);
                    }
                    whoUseHere.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(24), 400, 5), whoUseHere);
                    sendFeedback(source, new TranslatableText("command.here.feedback", whoUseHere.getName().asString()));
                    return 1;
                } catch (Exception e) {
                    sendError(source, new TranslatableText("command.here.failed.feedback"));

                    return - 1;
                }
            } else {
                sendError(source, new TranslatableText("here_command.false.rule.format"));
            }
            return 0;
        }));
        return this;
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

        return new TranslatableText("command.dhere", useHerePlayerName, "", DimensionUtil.getDimensionColor(dimension) + useHerePlayerName, DimensionUtil.getDimensionName(dimension), "§e" + xyz.getIntegerXYZ(), DimensionUtil.getDimensionName(convertTarget), "§d" + convertXYZ.getIntegerXYZ());
    }
}
