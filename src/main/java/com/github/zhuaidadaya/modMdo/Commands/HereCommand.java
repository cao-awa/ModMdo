package com.github.zhuaidadaya.modMdo.Commands;

import com.github.zhuaidadaya.modMdo.Lang.Language;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.*;

public class HereCommand implements Here {
    public int here(CommandContext<ServerCommandSource> context) {
        try {
            DimensionTips dimensionTips = new DimensionTips();
            PlayerManager p = context.getSource().getServer().getPlayerManager();
            for(String o : p.getPlayerNames()) {
                ServerPlayerEntity player = p.getPlayer(o);
                XYZ xyz = new XYZ(player.getX(), player.getY(), player.getZ());
                String dimension = player.getEntityWorld().getDimension().getEffects().getPath();
                player.sendMessage(new LiteralText(formatHereTip(dimension, xyz, player, dimensionTips)), false);
                player.addStatusEffect(new StatusEffectInstance(StatusEffect.byRawId(24), 400, 5), player);
            }
            context.getSource().sendFeedback(Text.of(formatHereFeedBack(context.getSource().getPlayer())), true);
            return 1;
        } catch (Exception e) {
            try {
                context.getSource().sendFeedback(Text.of(formatHereFailedFeedBack(context.getSource().getPlayer())), false);
            } catch (CommandSyntaxException ex) {

            }
            return - 1;
        }
    }

    @Override
    public String formatHereTip(String dimension, XYZ xyz,ServerPlayerEntity player, DimensionTips dimensionTips) {
        String playerName = player.getName().asString();
        Language getLang = getUserLanguage(player.getUuid());
        String format = languageDictionary.getWord(getLang, "command.here");
        String format_startWith = languageDictionary.getWord(getLang, "command.here.startWith");
        return String.format(format_startWith, playerName) + String.format(format, dimensionTips.getDimensionColor(dimension), playerName, dimensionTips.getDimensionName(getLang,dimension), xyz.getIntegerXYZ());
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
