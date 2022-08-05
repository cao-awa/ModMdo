package com.github.cao.awa.modmdo.utils.entity.player;

import com.mojang.authlib.*;

import java.util.*;

public class PlayerUtil {
    public static UUID getUUID(GameProfile profile) {
        return profile.getId();
    }
}
