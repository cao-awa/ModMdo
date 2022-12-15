package com.github.cao.awa.modmdo.utils.entity;

import net.minecraft.entity.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class EntityUtil {
    public static String getName(@Nullable Entity entity) {
        return entity == null ? "" : entity.getName().getString();
    }

    public static UUID getUUID(Entity entity) {
        return entity.getUuid();
    }
}
