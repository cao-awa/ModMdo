package com.github.cao.awa.modmdo.utils.entity;

import net.minecraft.entity.*;
import org.jetbrains.annotations.*;

public class EntityUtil {
    public static String getName(@Nullable Entity player) {
        return player == null ? "" : player.getName().getString();
    }
}
