package com.github.zhuaidadaya.modMdo.commands;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class HereCommand {
    public int here(CommandContext<ServerCommandSource> context) {
        try {
            DimensionTips dimensionTips = new DimensionTips();
            String whoUseHere = context.getSource().getPlayer().getName().getString();
            PlayerManager p = context.getSource().getServer().getPlayerManager();
            for(String o : p.getPlayerNames()) {
                ServerPlayerEntity player = p.getPlayer(o);
                int x = (int) player.getX();
                int y = (int) player.getY();
                int z = (int) player.getZ();
                String dimension = player.getEntityWorld().getDimension().getEffects().getPath();
                p.getPlayer(o).sendMessage(new LiteralText(dimensionTips.getDimensionColor(dimension) + whoUseHere + "在" + dimensionTips.getDimensionName(dimension) + ": [" + "x:" + x + ", " + "y:" + y + ", " + "z:" + z + "]"), false);
            }
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }
}
