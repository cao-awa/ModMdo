package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.login.token.Encryption.AES;
import com.github.zhuaidadaya.modMdo.login.token.ServerEncryptionToken;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class TokenCommand extends SimpleCommandOperation implements SimpleCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("token").requires(level -> level.hasPermissionLevel(4)).then(literal("regenerate").executes(token -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_TOKEN, getPlayer(token), this, token)) {
                    modMdoToken.setServerToken(ServerEncryptionToken.createServerEncryptionToken());
                    updateModMdoVariables();
                }
                return 4;
            }).then(literal("default").executes(def -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_TOKEN, getPlayer(def), this, def)) {
                    try {
                        generateDefault(tokenGenerateSize);
                    } catch (Exception e) {
                        sendError(def, new TranslatableText("token.regenerate.failed.format"));
                    }

                    updateModMdoVariables();
                }
                return 3;
            })).then(literal("ops").executes(ops -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_TOKEN, getPlayer(ops), this, ops)) {
                    try {
                        generateOps(tokenGenerateSize);
                    } catch (Exception e) {
                        sendError(ops, new TranslatableText("token.regenerate.failed.format"));
                    }

                    updateModMdoVariables();
                }
                return 2;
            })).then(literal("all").executes(all -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_TOKEN, getPlayer(all), this, all)) {

                    try {
                        generateAll(tokenGenerateSize);
                    } catch (Exception e) {
                        sendError(all, new TranslatableText("token.regenerate.failed.format"));
                    }
                    updateModMdoVariables();
                }
                return 1;
            }))).then(literal("size").then(literal("128").executes(setSize128 -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_TOKEN, getPlayer(setSize128), this, setSize128)) {

                    setGenerateSize(128);

                    sendFeedback(setSize128, formatSetTokenSize());
                }
                return 128;
            })).then(literal("256").executes(setSize256 -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_TOKEN, getPlayer(setSize256), this, setSize256)) {
                    setGenerateSize(256);

                    sendFeedback(setSize256, formatSetTokenSize());
                }
                return 256;
            })).then(literal("512").executes(setSize512 -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_TOKEN, getPlayer(setSize512), this, setSize512)) {
                    setGenerateSize(512);

                    sendFeedback(setSize512, formatSetTokenSize());
                }
                return 512;
            })).then(literal("1024").executes(setSize1024 -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_TOKEN, getPlayer(setSize1024), this, setSize1024)) {
                    setGenerateSize(1024);

                    sendFeedback(setSize1024, formatSetTokenSize());
                }
                return 1024;
            }))));
        });
    }

    public TranslatableText formatSetTokenSize() {
        return new TranslatableText("token.regenerate.set.size", tokenGenerateSize);
    }

    public void generateDefault(int size) throws Exception {
        modMdoToken.setServerToken(new ServerEncryptionToken(modMdoToken.getServerToken().setServerDefaultToken(new AES().randomGet(size))));
    }

    public void generateOps(int size) throws Exception {
        modMdoToken.setServerToken(new ServerEncryptionToken(modMdoToken.getServerToken().setServerOpsToken(new AES().randomGet(size))));
    }

    public void generateAll(int size) throws Exception {
        modMdoToken.setServerToken(ServerEncryptionToken.createServerEncryptionToken(size));
    }

    public void setGenerateSize(int size) {
        tokenGenerateSize = size;
    }
}
