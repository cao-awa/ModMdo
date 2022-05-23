package com.github.zhuaidadaya.modmdo.utils.player;

import net.minecraft.server.network.ServerPlayerEntity;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.getServerLevelPath;
import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.server;

public class PlayerUtil {
    public static long getPlayTime(ServerPlayerEntity player) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(getServerLevelPath(server) + "stats/" + player.getUuid().toString() + ".json"));

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
}
