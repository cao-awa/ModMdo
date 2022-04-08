package com.github.zhuaidadaya.modmdo.utils.dimension;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modmdo.storage.Variables.consoleTextFormat;
import static com.github.zhuaidadaya.modmdo.storage.Variables.minecraftTextFormat;

public class DimensionUtil {
    public static String getDimensionColor(String dimension) {
        String result;
        switch(dimension) {
            case "overworld" -> result = "§a";
            case "the_nether" -> result = "§c";
            case "the_end" -> result = "§f";
            default -> result = "";
        }
        return result;
    }

    public static String getDimensionKey(String dimension) {
        return "dimension." + dimension;
    }

    public static String getDimensionName(String dimension) {
        return consoleTextFormat.format(getDimensionKey(dimension));
    }

    public static String getDimension(ServerPlayerEntity player) {
        return player.getEntityWorld().getDimension().getEffects().getPath();
    }
}
