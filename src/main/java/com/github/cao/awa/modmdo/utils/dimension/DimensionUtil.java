package com.github.cao.awa.modmdo.utils.dimension;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import static com.github.cao.awa.modmdo.storage.SharedVariables.consoleTextFormat;

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
        return player.getEntityWorld().getDimension().effects().getPath();
    }

    public static String getDimension(World world) {
        return world.getDimension().effects().getPath();
    }
}
