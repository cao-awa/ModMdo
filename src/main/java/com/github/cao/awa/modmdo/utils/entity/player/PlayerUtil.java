package com.github.cao.awa.modmdo.utils.entity.player;

import com.github.cao.awa.modmdo.utils.entity.*;
import com.mojang.authlib.*;

import java.util.*;

public class PlayerUtil extends EntityUtil {
    public static UUID getUUID(GameProfile profile) {
        return profile.getId();
    }
}
