package com.github.zhuaidadaya.modMdo.Commands;

import com.github.zhuaidadaya.modMdo.Lang.Language;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.*;

public class DimensionTips {
    public String getDimensionColor(String dimension) {
        String result;
        switch(dimension) {
            case "overworld" -> result = "§a";
            case "the_nether" -> result = "§c";
            case "the_end" -> result = "§f";
            default -> result = "";
        }
        return result;
    }

    public String getDimensionName(Language getLang,String dimension) {
        String result;
        getLang = getLanguage(getLang);
        switch(dimension) {
            case "overworld" -> result = languageDictionary.getWord(getLang,"dimension.overworld");
            case "the_nether" -> result = languageDictionary.getWord(getLang,"dimension.the_nether");
            case "the_end" -> result = languageDictionary.getWord(getLang,"dimension.the_end");
            default -> result = "";
        }
        return result;
    }

    public String getDimension(ServerPlayerEntity player) {
        return player.getEntityWorld().getDimension().getEffects().getPath();
    }
}
