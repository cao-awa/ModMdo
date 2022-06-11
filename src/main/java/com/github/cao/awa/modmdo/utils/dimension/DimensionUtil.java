package com.github.cao.awa.modmdo.utils.dimension;

import net.minecraft.entity.*;
import net.minecraft.world.*;
import net.minecraft.world.dimension.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

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

    public static String getDimension(Entity player) {
        return getDimension(player.getEntityWorld());
    }

    public static String getDimension(World world) {
        return getDimension(world.getDimension());
    }

    public static String getDimension(DimensionType dimension) {
        return dimension.effects().getPath();
    }
}
