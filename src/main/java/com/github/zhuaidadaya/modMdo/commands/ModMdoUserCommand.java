package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.usr.User;
import com.github.zhuaidadaya.modMdo.usr.UserUtil;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import java.util.Locale;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class ModMdoUserCommand extends SimpleCommandOperation implements ConfigurableCommand {
    public void register() {
        init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("user").then(literal("hereMessage").executes(getHereReceive -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR, getPlayer(getHereReceive), this, getHereReceive)) {
                    ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
                    getHereReceive.getSource().sendFeedback(formatProfileReturnMessage("receiveHereMessage", getUserHereReceive(player.getUuid())), false);
                }
                return 2;
            }).then(literal("receive").executes(receive -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR, getPlayer(receive), this, receive)) {
                    ServerPlayerEntity player = receive.getSource().getPlayer();
                    setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveHereMessage", "receive");
                    receive.getSource().sendFeedback(receiveHereMessage(), false);
                }
                return 1;
            })).then(literal("rejection").executes(rejection -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR, getPlayer(rejection), this, rejection)) {
                    ServerPlayerEntity player = rejection.getSource().getPlayer();
                    setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveHereMessage", "rejection");
                    rejection.getSource().sendFeedback(rejectionHereMessage(), false);
                }
                return 0;
            }))).then(literal("deadMessage").executes(getHereReceive -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR, getPlayer(getHereReceive), this, getHereReceive)) {
                    ServerPlayerEntity player = getHereReceive.getSource().getPlayer();
                    getHereReceive.getSource().sendFeedback(formatProfileReturnMessage("receiveDeadMessage", getUserDeadMessageReceive(player.getUuid())), false);
                }
                return 2;
            }).then(literal("receive").executes(receive -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR, getPlayer(receive), this, receive)) {
                    ServerPlayerEntity player = receive.getSource().getPlayer();
                    setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "receive");
                    receive.getSource().sendFeedback(receiveDeadMessage(), false);
                }
                return 1;
            })).then(literal("rejection").executes(rejection -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR, getPlayer(rejection), this, rejection)) {
                    ServerPlayerEntity player = rejection.getSource().getPlayer();
                    setUserProfile(new User(player.getName().asString(), player.getUuid()), "receiveDeadMessage", "rejection");
                    rejection.getSource().sendFeedback(rejectionDeadMessage(), false);
                }
                return 0;
            }))).then(literal("objects").then(literal("joinServer").then(literal("follow").executes(joinServerFollow -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR_FOLLOW, getPlayer(joinServerFollow), this, joinServerFollow)) {
                    String joinGameFollowConfig = config.getConfigString("join_server_follow").toLowerCase(Locale.ROOT);
                    if(joinGameFollowConfig.equals("unable")) {
                        sendFeedback(joinServerFollow, new TranslatableText("follow.join.server.unable.rule.format"));
                    } else {
                        if(joinGameFollowConfig.equals("ops") & ! getPlayer(joinServerFollow).hasPermissionLevel(4)) {
                            sendFeedback(joinServerFollow, new TranslatableText("follow.join.server.ops.rule.format"));
                        } else {
                            addUserFollow(users.getUser(getPlayer(joinServerFollow)), "join_server_follow");
                        }
                    }
                }
                return 0;
            })).then(literal("unfollow").executes(joinServerUnfollow -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR_FOLLOW, getPlayer(joinServerUnfollow), this, joinServerUnfollow)) {
                    removeUserFollow(users.getUser(getPlayer(joinServerUnfollow)), "join_server_follow");
                }
                return 1;
            }))).then(literal("runCommand").then(literal("follow").executes(runCommandFollow -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR_FOLLOW, getPlayer(runCommandFollow), this, runCommandFollow)) {
                    String joinGameFollowConfig = config.getConfigString("run_command_follow").toLowerCase(Locale.ROOT);
                    if(joinGameFollowConfig.equals("unable")) {
                        sendFeedback(runCommandFollow, new TranslatableText("follow.run.command.unable.rule.format"));
                    } else {
                        if(joinGameFollowConfig.equals("ops") & ! getPlayer(runCommandFollow).hasPermissionLevel(4)) {
                            sendFeedback(runCommandFollow, new TranslatableText("follow.run.command.ops.rule.format"));
                        } else {
                            addUserFollow(users.getUser(getPlayer(runCommandFollow)), "run_command_follow");
                        }
                    }
                }
                return 0;
            })).then(literal("unfollow").executes(runCommandUnfollow -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_USR_FOLLOW, getPlayer(runCommandUnfollow), this, runCommandUnfollow)) {
                    removeUserFollow(users.getUser(getPlayer(runCommandUnfollow)), "run_command_follow");
                }
                return 1;
            })))));
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
        return new TranslatableText("dead.rejection");
    }

    public TranslatableText receiveDeadMessage() {
        return new TranslatableText("dead.receive");
    }


    public void init() {
        LOGGER.info("initializing user profiles");

        Object projectConf = config.getConfig("user_profiles");
        if(projectConf != null) {
            users = new UserUtil(new JSONObject(projectConf.toString()));
        } else {
            users = new UserUtil();
            config.set("user_profiles", new JSONObject());
        }

        updateUserProfiles();

        LOGGER.info("initialized user profiles");
    }
}
