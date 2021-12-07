package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.MCH.Utils.Config.Config;
import com.github.zhuaidadaya.modMdo.cavas.Cava;
import com.github.zhuaidadaya.modMdo.cavas.CavaUtil;
import com.github.zhuaidadaya.modMdo.usr.User;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.json.JSONObject;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CavaCommand {
    public void register() {
        initCavas();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("cava").executes(getCava -> {
                ServerCommandSource source = getCava.getSource();
                ServerPlayerEntity player = source.getPlayer();

                if(enableCava) {
                    try {
                        source.sendFeedback(Text.of(String.format(languageDictionary.getWord(getUserLanguage(player), "cava.format"), getCava(users.getUser(player)).getMessage(), player.getName().asString())), false);
                    } catch (Exception e) {
                        source.sendError(Text.of(languageDictionary.getWord(getUserLanguage(player), "command.cava.noCava")));
                    }
                } else {
                    source.sendError(Text.of(formatCavaDisabled(player)));
                }

                return 0;
            }).then(literal("create").then(argument("cava_message", StringArgumentType.greedyString()).executes(createCava -> {
                ServerCommandSource source = createCava.getSource();
                ServerPlayerEntity player = source.getPlayer();
                if(enableCava) {
                    String cavaMessage = createCava.getInput();

                    try {
                        Cava cava = cavas.createCava(users.getUser(player), cavaMessage.substring(13));

                        LOGGER.info(String.format(languageDictionary.getWord(language, "cava.created"), player.getName().asString(), player.getUuid(), cava.getID()));

                        source.sendFeedback(Text.of(String.format(languageDictionary.getWord(getUserLanguage(player), "cava.feedback.created"), cava.getID())), false);
                    } catch (IllegalArgumentException e) {
                        source.sendError(Text.of(languageDictionary.getWord(getUserLanguage(player), "cava.create.failed.alreadyExists")));
                    } catch (Exception e) {
                        source.sendError(Text.of(languageDictionary.getWord(getUserLanguage(player), "cava.create.failed")));
                    }
                } else {
                    source.sendError(Text.of(formatCavaDisabled(player)));
                }

                return 1;
            }))).then(literal("deleteLast").requires(level -> level.hasPermissionLevel(4)).executes(deleteCava -> {
                ServerCommandSource source = deleteCava.getSource();
                ServerPlayerEntity player = source.getPlayer();

                if(enableCava) {
                    try {
                        String cavaID = users.getUserConfig(player.getUuid(), "lastCava").toString();

                        cavas.deleteCava(cavaID);

                        LOGGER.info(String.format(languageDictionary.getWord(language, "cava.deleted"), player.getName().asString(), player.getUuid(), cavaID));

                        source.sendFeedback(Text.of(String.format(languageDictionary.getWord(getUserLanguage(player), "cava.feedback.deleted"), cavaID)), false);
                    } catch (Exception e) {
                        source.sendError(Text.of(languageDictionary.getWord(getUserLanguage(player), "cava.delete.failed")));
                    }
                } else {
                    source.sendError(Text.of(formatCavaDisabled(player)));
                }

                return 2;
            })));
        });
    }

    public String formatCavaDisabled(ServerPlayerEntity player) {
        return languageDictionary.getWord(getUserLanguage(player), "cava.disabled");
    }

    public Cava getCava(User user) {
        Cava cava = cavas.get();
        setUserProfile(user, "lastCava", cava.getID());

        updateUserProfiles();
        return cava;
    }

    public void initCavas() {
        LOGGER.info("initializing cavas");
        Config<Object, Object> projectConf = config.getConfig("cavas");
        if(projectConf != null) {
            cavas = new CavaUtil(new JSONObject(projectConf.getValue()));
        } else {
            cavas = new CavaUtil();
            config.set("cavas", new JSONObject());
        }
        LOGGER.info("initialized cavas");

        updateCavas();
    }
}
