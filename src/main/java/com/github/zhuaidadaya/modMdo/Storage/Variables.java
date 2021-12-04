package com.github.zhuaidadaya.modMdo.Storage;

import com.github.zhuaidadaya.MCH.Utils.Config.ConfigUtil;
import com.github.zhuaidadaya.modMdo.Lang.Language;
import com.github.zhuaidadaya.modMdo.Lang.LanguageDictionary;
import com.github.zhuaidadaya.modMdo.Projects.ProjectUtil;
import com.github.zhuaidadaya.modMdo.Usr.UserUtil;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class Variables {
    public static final Logger LOGGER = LogManager.getLogger("ModMdo");
    public static String entrust = "ModMdo";
    public static Language language = Language.CHINESE;
    public static LanguageDictionary languageDictionary;
    public static boolean enableHereCommand = true;
    public static boolean enableDeadMessage = true;
    public static ConfigUtil config;
    public static ProjectUtil projects;
    public static UserUtil users;
    public static String motd = "";
    public static MinecraftServer server;
    public static boolean backing = false;

    public static void updateUserProfiles() {
        config.set("user_profiles", users.toJSONObject());
    }

    public static void updateProjects() {
        config.set("projects", projects.toJSONObject());
    }

    public static Language getLanguage() {
        return language;
    }

    public static Language getLanguage(Language lang) {
        return lang == null ? language : lang;
    }

    public static Language getUserLanguage(UUID userUUID) {
        try {
            return getLanguage(Language.getLanguageForName(users.getUserConfig(userUUID, "language").toString()));
        } catch (Exception e) {
            return language;
        }
    }

    public static boolean isUserHereReceive(UUID userUUID) {
        try {
            return users.getUserConfig(userUUID, "receiveHereMessage").toString().equals("receive");
        } catch (Exception e) {
            return enableHereCommand;
        }
    }

    public static String getUserHereReceive(UUID userUUID) {
        try {
            return users.getUserConfig(userUUID, "receiveHereMessage").toString();
        } catch (Exception e) {
            return enableHereCommand ? "receive" : "rejected";
        }
    }

    public static boolean isUserDeadMessageReceive(UUID userUUID) {
        try {
            return users.getUserConfig(userUUID, "receiveDeadMessage").toString().equals("receive");
        } catch (Exception e) {
            return enableDeadMessage;
        }
    }

    public static String getUserDeadMessageReceive(UUID userUUID) {
        try {
            return users.getUserConfig(userUUID, "receiveDeadMessage").toString();
        } catch (Exception e) {
            return enableDeadMessage ? "receive": "rejected";
        }
    }
}
