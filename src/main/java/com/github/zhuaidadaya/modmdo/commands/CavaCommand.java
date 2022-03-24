package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.cavas.Cava;
import com.github.zhuaidadaya.modmdo.cavas.CavaUtil;
import com.github.zhuaidadaya.modmdo.usr.User;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CavaCommand extends SimpleCommandOperation implements ConfigurableCommand {
    public void register() {
        init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("cava").executes(cava -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CAVA, getPlayer(cava), this, cava)) {
                    ServerCommandSource source = cava.getSource();

                    if(enableCava) {
                        try {
                            source.sendFeedback(formatCavaTip(getPlayer(cava)), false);
                        } catch (Exception e) {
                            source.sendError(formatNoCava());
                        }
                    } else {
                        source.sendError(formatCavaDisabled());
                    }
                }
                return 0;
            }).then(literal("create").then(argument("cava_message", StringArgumentType.greedyString()).executes(createCava -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CAVA, getPlayer(createCava), this, createCava)) {
                    ServerCommandSource source = createCava.getSource();
                    ServerPlayerEntity player = getPlayer(createCava);
                    if(enableCava) {
                        String cavaMessage = createCava.getInput();

                        try {
                            Cava cava = cavas.createCava(users.getUser(player), cavaMessage.substring(13));

                            LOGGER.info(String.format("player %s(%s) created a Cava, Cava id: %s", player.getName().asString(), player.getUuid(), cava.getID()));

                            source.sendFeedback(formatCavaCreated(cava.getID()), false);
                        } catch (IllegalArgumentException e) {
                            source.sendError(formatCavaExists());
                        } catch (Exception e) {
                            source.sendError(formatCavaCreateFail());
                        }
                    } else {
                        source.sendError(formatCavaDisabled());
                    }
                }
                return 1;
            }))).then(literal("deleteLast").requires(level -> level.hasPermissionLevel(4)).executes(deleteCava -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_CAVA, getPlayer(deleteCava), this, deleteCava)) {
                    ServerCommandSource source = deleteCava.getSource();
                    ServerPlayerEntity player = source.getPlayer();

                    if(enableCava) {
                        try {
                            String cavaID = users.getUserConfig(player.getUuid().toString(), "lastCava").toString();

                            cavas.deleteCava(cavaID);

                            LOGGER.info(String.format("player %s(%s) deleted a Cava, Cava id: %s", player.getName().asString(), player.getUuid(), cavaID));

                            source.sendFeedback(formatCavaDeleted(cavaID), false);
                        } catch (Exception e) {
                            source.sendError(formatCavaDeleteFail());
                        }
                    } else {
                        source.sendError(formatCavaDisabled());
                    }
                }
                return 2;
            })));
        });
    }

    public TranslatableText formatCavaDeleteFail() {
        return new TranslatableText( "cava.delete.failed");
    }

    public TranslatableText formatCavaDeleted(String cavaID) {
        return new TranslatableText("cava.feedback.deleted", cavaID);
    }

    public TranslatableText formatCavaCreateFail() {
        return new TranslatableText("cava.create.failed");
    }

    public TranslatableText formatCavaExists() {
        return new TranslatableText("cava.create.failed.alreadyExists");
    }

    public TranslatableText formatCavaCreated(String cavaID) {
        return new TranslatableText("cava.feedback.created", cavaID);
    }

    public TranslatableText formatNoCava() {
        return new TranslatableText("command.cava.noCava");
    }

    public TranslatableText formatCavaTip(ServerPlayerEntity player) {
        return new TranslatableText("cava.format", getCava(users.getUser(player)).getMessage(), player.getName().asString());
    }

    public TranslatableText formatCavaDisabled() {
        return new TranslatableText("cava.disable");
    }

    public Cava getCava(User user) {
        Cava cava = cavas.get();
        setUserProfile(user, "lastCava", cava.getID());

        updateUserProfiles();
        return cava;
    }

    public void init() {
        Object projectConf = config.getConfig("cavas");
        if(projectConf != null) {
            cavas = new CavaUtil(new JSONObject(projectConf.toString()));
        } else {
            cavas = new CavaUtil();
            config.set("cavas", new JSONObject());
        }

        updateCavas();
    }
}
