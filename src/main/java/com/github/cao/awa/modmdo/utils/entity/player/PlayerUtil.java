package com.github.cao.awa.modmdo.utils.entity.player;

import com.github.cao.awa.modmdo.storage.*;
import com.mojang.authlib.*;
import net.minecraft.server.network.*;
import org.json.*;

import java.io.*;
import java.util.*;

public class PlayerUtil {
    public static long getPlayTime(ServerPlayerEntity player) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(SharedVariables.getServerLevelPath(SharedVariables.server) + "stats/" + player.getUuid().toString() + ".json"));

            player.getStatHandler().save();

            String cache;
            StringBuilder builder = new StringBuilder();
            while ((cache = reader.readLine()) != null) {
                builder.append(cache);
            }

            JSONObject source = new JSONObject(builder.toString());
            JSONObject stat = source.getJSONObject("stats");

            return getCustomStat("minecraft:play_time", stat);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long getCustomStat(String object, JSONObject stat) {
        long count = 0;
        try {
            JSONObject custom = stat.getJSONObject("minecraft:custom");

            try {
                count = custom.getLong(object);
            } catch (Exception e) {

            }
        } catch (Exception e) {

        }
        return count;
    }

    public static UUID getId(ServerPlayerEntity player) {
        return getId(player.getGameProfile());
    }

    public static UUID getId(GameProfile profile) {
        return profile.getId();
    }
}
