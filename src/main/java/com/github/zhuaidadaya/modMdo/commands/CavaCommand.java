package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.MCH.utils.config.Config;
import com.github.zhuaidadaya.modMdo.cavas.Cava;
import com.github.zhuaidadaya.modMdo.cavas.CavaUtil;
import com.github.zhuaidadaya.modMdo.lang.Language;
import com.github.zhuaidadaya.modMdo.usr.User;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CavaCommand implements CavaCommandFormat {
    public void register() {
        initCavas();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("cava").executes(getCava -> {
                ServerCommandSource source = getCava.getSource();
                ServerPlayerEntity player = source.getPlayer();

                if(enableCava) {
                    try {
                        source.sendFeedback(formatCavaTip(player), false);
                    } catch (Exception e) {
                        source.sendError(formatNoCava());
                    }
                } else {
                    source.sendError(formatCavaDisabled());
                }

                return 0;
            }).then(literal("create").then(argument("cava_message", StringArgumentType.greedyString()).executes(createCava -> {
                ServerCommandSource source = createCava.getSource();
                ServerPlayerEntity player = source.getPlayer();
                if(enableCava) {
                    String cavaMessage = createCava.getInput();

                    try {
                        Cava cava = cavas.createCava(users.getUser(player), cavaMessage.substring(13));

                        LOGGER.info(String.format((language == Language.CHINESE ? "玩家 %s(%s) 创建了一个Cava, Cava id: %s" : "player %s(%s) created a Cava, Cava id: %s"), player.getName().asString(), player.getUuid(), cava.getID()));

                        source.sendFeedback(formatCavaCreated(cava.getID()), false);
                    } catch (IllegalArgumentException e) {
                        source.sendError(formatCavaExists());
                    } catch (Exception e) {
                        source.sendError(formatCavaCreateFail());
                    }
                } else {
                    source.sendError(formatCavaDisabled());
                }

                return 1;
            }))).then(literal("deleteLast").requires(level -> level.hasPermissionLevel(4)).executes(deleteCava -> {
                ServerCommandSource source = deleteCava.getSource();
                ServerPlayerEntity player = source.getPlayer();

                if(enableCava) {
                    try {
                        String cavaID = users.getUserConfig(player.getUuid(), "lastCava").toString();

                        cavas.deleteCava(cavaID);

                        LOGGER.info(String.format((language == Language.CHINESE ? "玩家 %s(%s) 删除了一个Cava, Cava id: %s" : "player %s(%s) deleted a Cava, Cava id: %s"), player.getName().asString(), player.getUuid(), cavaID));

                        source.sendFeedback(formatCavaDeleted(cavaID), false);
                    } catch (Exception e) {
                        source.sendError(formatCavaDeleteFail());
                    }
                } else {
                    source.sendError(formatCavaDisabled());
                }

                return 2;
            })));
        });
    }

    @Override
    public TranslatableText formatCavaDeleteFail() {
        return new TranslatableText( "cava.delete.failed");
    }

    @Override
    public TranslatableText formatCavaDeleted(String cavaID) {
        return new TranslatableText("cava.feedback.deleted", cavaID);
    }

    @Override
    public TranslatableText formatCavaCreateFail() {
        return new TranslatableText("cava.create.failed");
    }

    @Override
    public TranslatableText formatCavaExists() {
        return new TranslatableText("cava.create.failed.alreadyExists");
    }

    @Override
    public TranslatableText formatCavaCreated(String cavaID) {
        return new TranslatableText("cava.feedback.created", cavaID);
    }

    @Override
    public TranslatableText formatNoCava() {
        return new TranslatableText("command.cava.noCava");
    }

    @Override
    public TranslatableText formatCavaTip(ServerPlayerEntity player) {
        return new TranslatableText("cava.format", getCava(users.getUser(player)).getMessage(), player.getName().asString());
    }

    @Override
    public TranslatableText formatCavaDisabled() {
        return new TranslatableText("cava.disable");
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
