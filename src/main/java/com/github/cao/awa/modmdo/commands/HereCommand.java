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

public class HereCommand extends SimpleCommand {
    public HereCommand register() {
        SharedVariables.commandRegister.register(literal("here").executes(here -> {
            ServerCommandSource source = here.getSource();
            if (SharedVariables.enableHereCommand) {
                try {
                    ServerPlayerEntity whoUseHere = source.getPlayer();
                    PlayerManager p = source.getServer().getPlayerManager();
                    XYZ xyz = new XYZ(whoUseHere.getX(), whoUseHere.getY(), whoUseHere.getZ());
                    String dimension = whoUseHere.getEntityWorld().getDimension().getEffects().getPath();
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

        return new TranslatableText("command.here", useHerePlayerName, "", DimensionUtil.getDimensionColor(dimension) + useHerePlayerName, DimensionUtil.getDimensionName(dimension), "§e" + xyz.getIntegerXYZ());
    }
}
