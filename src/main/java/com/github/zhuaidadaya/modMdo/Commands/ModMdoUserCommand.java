package com.github.zhuaidadaya.modMdo.Commands;

import com.github.zhuaidadaya.MCH.Utils.Config.Config;
import com.github.zhuaidadaya.modMdo.Lang.Language;
import com.github.zhuaidadaya.modMdo.Usr.User;
import com.github.zhuaidadaya.modMdo.Usr.UserUtil;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.json.JSONObject;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class ModMdoUserCommand {
    public void user() {
        initUserProfile();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("user").then(literal("language").then(literal("english").executes(english -> {
                ServerPlayerEntity player = english.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "language", "English");
                english.getSource().sendFeedback(Text.of(formatChangeLanguage(Language.ENGLISH)), false);
                return 1;
            })).then(literal("chinese").executes(chinese -> {
                ServerPlayerEntity player = chinese.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "language", "Chinese");
                chinese.getSource().sendFeedback(Text.of(formatChangeLanguage(Language.CHINESE)), false);
                return 0;
            }))));
        });
    }

    public String formatChangeLanguage(Language feedbackLanguage) {
        return String.format(languageDictionary.getWord(feedbackLanguage, "language.change"), languageDictionary.getWord(feedbackLanguage, "languages." + feedbackLanguage.getName()));
    }

    public void setUserProfile(User user, String changeKey, String changeValue) {
        JSONObject userInfo;
        try {
            userInfo = users.getJSONObject(user.getID());
        } catch (Exception e) {
            userInfo = new JSONObject().put("uuid", user.getID()).put("name", user.getName());
        }
        userInfo.put(changeKey, changeValue);
        users.put(user.getID(), userInfo);

        updateUserProfiles();
    }

    public void initUserProfile() {
        LOGGER.info("initializing user profiles");

        Config<Object, Object> projectConf = config.getConfig("user_profiles");
        if(projectConf != null) {
            users = new UserUtil(new JSONObject(projectConf.getValue()));
        } else {
            users = new UserUtil();
            config.set("user_profiles", new JSONObject());
        }

        LOGGER.info("initialized user profiles");
    }
}
