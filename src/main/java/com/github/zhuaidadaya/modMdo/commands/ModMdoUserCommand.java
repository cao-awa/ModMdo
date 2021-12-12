package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.MCH.utils.config.Config;
import com.github.zhuaidadaya.modMdo.usr.User;
import com.github.zhuaidadaya.modMdo.usr.UserUtil;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class ModMdoUserCommand {
    public void register() {
        initUserProfile();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("user").then(literal("receiveHereMessage").executes(getHereReceive -> {
                ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
                getHereReceive.getSource().sendFeedback(formatProfileReturnMessage("receiveHereMessage", getUserHereReceive(player.getUuid())), false);

                return 2;
            }).then(literal("receive").executes(receive -> {
                ServerPlayerEntity player = receive.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveHereMessage", "receive");
                receive.getSource().sendFeedback(receiveHereMessage(), false);

                return 1;
            })).then(literal("rejection").executes(rejection -> {
                ServerPlayerEntity player = rejection.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveHereMessage", "rejection");
                rejection.getSource().sendFeedback(rejectionHereMessage(), false);

                return 0;
            }))));

            dispatcher.register(literal("user").then(literal("receiveDeadMessage").executes(getHereReceive -> {
                ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
                getHereReceive.getSource().sendFeedback(formatProfileReturnMessage( "receiveDeadMessage", getUserDeadMessageReceive(player.getUuid())), false);

                return 2;
            }).then(literal("receive").executes(receive -> {
                ServerPlayerEntity player = receive.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "receive");
                receive.getSource().sendFeedback(receiveDeadMessage(), false);

                return 1;
            })).then(literal("rejection").executes(rejection -> {
                ServerPlayerEntity player = rejection.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "rejection");
                rejection.getSource().sendFeedback(rejectionDeadMessage(), false);

                return 0;
            }))));
        });
    }

    public TranslatableText formatProfileReturnMessage(String profileKey, String profileValue) {
        return new TranslatableText("profile.feedback", profileKey, profileValue);
    }

    public TranslatableText rejectionHereMessage() {
        return new TranslatableText("command.here.rejection");
    }

    public TranslatableText receiveHereMessage() {
        return new TranslatableText("command.here.receive");
    }

    public TranslatableText rejectionDeadMessage() {
        return new TranslatableText( "dead.rejection");
    }

    public TranslatableText receiveDeadMessage() {
        return new TranslatableText("dead.receive");
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
