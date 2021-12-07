package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.lang.Language;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class HereCommand implements Here {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("here").executes(context -> {
                ServerCommandSource source = context.getSource();
                if(enableHereCommand) {
                    try {
                        DimensionTips dimensionTips = new DimensionTips();
                        ServerPlayerEntity whoUseHere = source.getPlayer();
                        PlayerManager p = source.getServer().getPlayerManager();
                        XYZ xyz = new XYZ(whoUseHere.getX(), whoUseHere.getY(), whoUseHere.getZ());
                        String dimension = whoUseHere.getEntityWorld().getDimension().getEffects().getPath();
                        for(String o : p.getPlayerNames()) {
                            ServerPlayerEntity player = p.getPlayer(o);
                            LiteralText hereMessage = new LiteralText(formatHereTip(dimension, xyz, player, dimensionTips, whoUseHere));
                            if(isUserHereReceive(player.getUuid())) {
                                player.sendMessage(hereMessage, false);
                            }
                        }
                        whoUseHere.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(24), 400, 5), whoUseHere);
                        source.sendFeedback(Text.of(formatHereFeedBack(whoUseHere)), true);
                        return 1;
                    } catch (Exception e) {
                        try {
                            source.sendError(Text.of(formatHereFailedFeedBack(source.getPlayer())));
                        } catch (CommandSyntaxException ex) {

                        }
                        return - 1;
                    }
                } else {
                    source.sendError(Text.of(formatHereDisabled(source.getPlayer())));
                }

                return 0;
            }));
        });
    }

    public String formatHereDisabled(ServerPlayerEntity player) {
        return languageDictionary.getWord(getUserLanguage(player), "here.disabled");
    }

    @Override
    public String formatHereTip(String dimension, XYZ xyz, ServerPlayerEntity player, DimensionTips dimensionTips, ServerPlayerEntity whoUseHere) {
        String playerName = player.getName().asString();
        String useHerePlayerName = whoUseHere.getName().asString();
        Language getLang = getUserLanguage(player.getUuid());
        String format = languageDictionary.getWord(getLang, "command.here");
        String format_startWith = languageDictionary.getWord(getLang, "command.here.startWith");
        return String.format(format_startWith, useHerePlayerName) + String.format(format, dimensionTips.getDimensionColor(dimension), useHerePlayerName, dimensionTips.getDimensionName(getLang, dimension), xyz.getIntegerXYZ());
    }

    @Override
    public String formatHereFeedBack(ServerPlayerEntity player) {
        String playerName = player.getName().asString();
        String format = languageDictionary.getWord(getUserLanguage(player.getUuid()), "command.here.feedback");
        return String.format(format, playerName);
    }

    @Override
    public String formatHereFailedFeedBack(ServerPlayerEntity player) {
        return languageDictionary.getWord(getUserLanguage(player.getUuid()), "command.here.failed.feedback");
    }
}
