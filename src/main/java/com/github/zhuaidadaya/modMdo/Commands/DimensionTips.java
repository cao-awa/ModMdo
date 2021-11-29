package com.github.zhuaidadaya.modMdo.Commands;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.language;
import static com.github.zhuaidadaya.modMdo.Storage.Variables.languageDictionary;

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

    public String getDimensionName(String dimension) {
        String result;
        switch(dimension) {
            case "overworld" -> result = languageDictionary.getWord(language,"overworld");
            case "the_nether" -> result = languageDictionary.getWord(language,"the_nether");
            case "the_end" -> result = languageDictionary.getWord(language,"the_end");
            default -> result = "";
        }
        return result;
    }
}
