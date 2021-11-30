package com.github.zhuaidadaya.modMdo.Storage;

import com.github.zhuaidadaya.MCH.Utils.Config.ConfigUtil;
import com.github.zhuaidadaya.modMdo.Lang.Language;
import com.github.zhuaidadaya.modMdo.Lang.LanguageDictionary;
import com.github.zhuaidadaya.modMdo.Usr.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.UUID;

public class Variables {
    public static final Logger LOGGER = LogManager.getLogger("ModMdo");
    public static String entrust = "ModMdo";
    public static Language language = Language.CHINESE;
    public static LanguageDictionary languageDictionary;
    public static ConfigUtil config;
    public static JSONObject projects;
    public static UserUtil users;

    public static void updateUserProfiles() {
        config.set("user_profiles", users.toJSONObject());
    }

    public static Language getLanguage() {
        return language;
    }

    public static Language getLanguage(Language lang) {
        return lang == null ? language : lang;
    }

    public static Language getUserLanguage(UUID userUUID) {
        return getLanguage(Language.getLanguageForName(users.getUserConfig(userUUID,"language").toString()));
    }
}
