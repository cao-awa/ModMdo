package com.github.zhuaidadaya.modMdo.Commands;

import com.github.zhuaidadaya.modMdo.Lang.Language;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class DimensionHereCommand implements Here {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("dhere").executes(context -> {
                ServerCommandSource source = context.getSource();
                if(enableHereCommand) {
                    try {
                        DimensionTips dimensionTips = new DimensionTips();
                        ServerPlayerEntity whoUseHere = source.getPlayer();
                        PlayerManager p = source.getServer().getPlayerManager();
                        XYZ xyz = new XYZ(whoUseHere.getX(), whoUseHere.getY(), whoUseHere.getZ());
                        String dimension = dimensionTips.getDimension(whoUseHere);
                        for(String o : p.getPlayerNames()) {
                            ServerPlayerEntity player = p.getPlayer(o);
                            LiteralText hereMessage = new LiteralText(formatHereTip(dimension, xyz, player, dimensionTips, whoUseHere));
                            if(isUserHereReceive(player.getUuid())) {
                                player.sendMessage(hereMessage, false);
                            }
                        }
                        whoUseHere.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(24), 400, 5), whoUseHere);
                        source.sendFeedback(Text.of(formatHereFeedBack(source.getPlayer())), true);
                        return 1;
                    } catch (Exception e) {
                        try {
                            source.sendError(Text.of(formatHereFailedFeedBack(source.getPlayer())));
                        } catch (Exception ex) {

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
        String convertTarget = "";
        switch(dimension) {
            case "overworld" -> convertTarget = "the_nether";
            case "the_nether" -> convertTarget = "overworld";
            case "the_end" -> {
                throw new IllegalArgumentException();
            }
        }
        Language getLang = getUserLanguage(player);
        String format = languageDictionary.getWord(getLang, "command.dhere");
        String format_startWith = languageDictionary.getWord(getLang, "command.here.startWith");
        XYZ convertXYZ = xyz.clone();
        if(convertTarget.equals("the_nether")) {
            convertXYZ.divideXZ(8, 8);
        } else {
            convertXYZ.multiplyXZ(8, 8);
        }
        return String.format(format_startWith, useHerePlayerName) + String.format(format, dimensionTips.getDimensionColor(dimension), useHerePlayerName, dimensionTips.getDimensionName(getLang, dimension), xyz.getIntegerXYZ(), languageDictionary.getWord(getLang, "dimension." + convertTarget), convertXYZ.getIntegerXYZ());
    }

    @Override
    public String formatHereFeedBack(ServerPlayerEntity player) {
        String playerName = player.getName().asString();
        Language getLang = getUserLanguage(player.getUuid());
        getLang = getLang == null ? language : getLang;
        String format = languageDictionary.getWord(getLang, "command.here.feedback");
        return String.format(format, playerName);
    }

    @Override
    public String formatHereFailedFeedBack(ServerPlayerEntity player) {
        return languageDictionary.getWord(getUserLanguage(player.getUuid()), "command.here.failed.feedback");
    }
}
