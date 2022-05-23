package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.utils.usr.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.json.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class ModMdoUserCommand extends ConfigurableCommand<ModMdoUserCommand> {
    public ModMdoUserCommand init() {
        Object projectConf = config.getConfig("user_profiles");
        if (projectConf != null) {
            users = new UserUtil(new JSONObject(projectConf.toString()));
        } else {
            users = new UserUtil();
            config.set("user_profiles", new JSONObject());
        }

        updateUserProfiles();

        return this;
    }

    public ModMdoUserCommand register() {
        commandRegister.register(literal("user").then(literal("hereMessage").executes(getHereReceive -> {
            if (commandApplyToPlayer(1, getPlayer(getHereReceive), getHereReceive)) {
                ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
                getHereReceive.getSource().sendFeedback(formatProfileReturnMessage("receiveHereMessage", getUserHereReceive(player.getUuid())), false);
            }
            return 2;
        }).then(literal("receive").executes(receive -> {
            if (commandApplyToPlayer(1, getPlayer(receive), receive)) {
                ServerPlayerEntity player = receive.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveHereMessage", "receive");
                receive.getSource().sendFeedback(receiveHereMessage(), false);
            }
            return 1;
        })).then(literal("rejection").executes(rejection -> {
            if (commandApplyToPlayer(1, getPlayer(rejection), rejection)) {
                ServerPlayerEntity player = rejection.getSource().getPlayer();
                setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveHereMessage", "rejection");
                rejection.getSource().sendFeedback(rejectionHereMessage(), false);
            }
            return 0;
        }))).then(literal("deadMessage").executes(getHereReceive -> {
            if (commandApplyToPlayer(1, getPlayer(getHereReceive), getHereReceive)) {
                ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
                getHereReceive.getSource().sendFeedback(formatProfileReturnMessage("receiveDeadMessage", getUserDeadMessageReceive(player.getUuid())), false);
            }
            return 2;
        }).then(literal("receive").executes(receive -> {
            if (commandApplyToPlayer(1, getPlayer(receive), receive)) {
                ServerPlayerEntity player = receive.getSource().getPlayer();
                setUserProfile(users.getUser(player), "receiveDeadMessage", "receive");
                receive.getSource().sendFeedback(receiveDeadMessage(), false);
            }
            return 1;
        })).then(literal("rejection").executes(rejection -> {
            if (commandApplyToPlayer(1, getPlayer(rejection), rejection)) {
                ServerPlayerEntity player = rejection.getSource().getPlayer();
                setUserProfile(users.getUser(player), "receiveDeadMessage", "rejection");
                rejection.getSource().sendFeedback(rejectionDeadMessage(), false);
            }
            return 0;
        }))));
        return this;
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
        return new TranslatableText("dead.rejection");
    }

    public TranslatableText receiveDeadMessage() {
        return new TranslatableText("dead.receive");
    }
}
