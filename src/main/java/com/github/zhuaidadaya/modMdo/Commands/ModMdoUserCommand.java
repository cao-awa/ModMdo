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
    public void register() {
        initUserProfile();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("user").then(literal("language").executes(getLang -> {
                ServerPlayerEntity player = getLang.getSource().getPlayer();
                getLang.getSource().sendFeedback(Text.of(formatProfileReturnMessage(getUserLanguage(player.getUuid()), "language", getUserLanguage(player.getUuid()).toString())), false);

                return 2;
            }).then(literal("english").executes(english -> {
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

            dispatcher.register(literal("user").then(literal("receiveHereMessage").executes(getHereReceive -> {
                ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
                getHereReceive.getSource().sendFeedback(Text.of(formatProfileReturnMessage(getUserLanguage(player.getUuid()), "receiveHereMessage", getUserHereReceive(player.getUuid()))), false);

                return 2;
            }).then(literal("receive").executes(receive -> {
                ServerPlayerEntity player = receive.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveHereMessage", "receive");
                receive.getSource().sendFeedback(Text.of(receiveHereMessage(getUserLanguage(player.getUuid()))), false);

                return 1;
            })).then(literal("rejection").executes(rejection -> {
                ServerPlayerEntity player = rejection.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveHereMessage", "rejection");
                rejection.getSource().sendFeedback(Text.of(rejectionHereMessage(getUserLanguage(player.getUuid()))), false);

                return 0;
            }))));

            dispatcher.register(literal("user").then(literal("receiveDeadMessage").executes(getHereReceive -> {
                ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
                getHereReceive.getSource().sendFeedback(Text.of(formatProfileReturnMessage(getUserLanguage(player.getUuid()), "receiveDeadMessage", getUserDeadMessageReceive(player.getUuid()))), false);

                return 2;
            }).then(literal("receive").executes(receive -> {
                ServerPlayerEntity player = receive.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "receive");
                receive.getSource().sendFeedback(Text.of(receiveDeadMessage(getUserLanguage(player.getUuid()))), false);

                return 1;
            })).then(literal("rejection").executes(rejection -> {
                ServerPlayerEntity player = rejection.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "rejection");
                rejection.getSource().sendFeedback(Text.of(rejectionDeadMessage(getUserLanguage(player.getUuid()))), false);

                return 0;
            }))));
        });
    }

    public String formatProfileReturnMessage(Language feedBackLanguage, String profileKey, String profileValue) {
        return String.format(languageDictionary.getWord(feedBackLanguage, "profile.feedback"), profileKey, profileValue);
    }

    public String rejectionHereMessage(Language feedbackLanguage) {
        return languageDictionary.getWord(feedbackLanguage, "command.here.rejection");
    }

    public String receiveHereMessage(Language feedbackLanguage) {
        return languageDictionary.getWord(feedbackLanguage, "command.here.receive");
    }

    public String rejectionDeadMessage(Language feedbackLanguage) {
        return languageDictionary.getWord(feedbackLanguage, "dead.rejection");
    }

    public String receiveDeadMessage(Language feedbackLanguage) {
        return languageDictionary.getWord(feedbackLanguage, "dead.receive");
    }

    public String formatChangeLanguage(Language feedbackLanguage) {
        return String.format(languageDictionary.getWord(feedbackLanguage, "language.change"), languageDictionary.getWord(feedbackLanguage, "languages." + feedbackLanguage.getName()));
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

        updateUserProfiles();

        LOGGER.info("initialized user profiles");
    }
}
