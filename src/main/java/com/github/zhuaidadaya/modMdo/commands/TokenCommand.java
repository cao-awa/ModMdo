package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.token.Encryption.AES;
import com.github.zhuaidadaya.modMdo.token.ServerEncryptionToken;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.literal;

public class TokenCommand {
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("token").requires(level -> level.hasPermissionLevel(4)).then(literal("regenerate").executes(getHereReceive -> {
                modMdoToken.setServerToken(ServerEncryptionToken.createServerEncryptionToken());
                updateModMdoVariables();

                return 4;
            }).then(literal("default").executes(def -> {
                try {
                    generateDefault(tokenGenerateSize);
                } catch (Exception e) {
                    def.getSource().sendError(new TranslatableText("token.regenerate.failed.format"));
                }

                updateModMdoVariables();

                return 3;
            })).then(literal("ops").executes(ops -> {
                try {
                    generateOps(tokenGenerateSize);
                } catch (Exception e) {
                    ops.getSource().sendError(new TranslatableText("token.regenerate.failed.format"));
                }

                updateModMdoVariables();

                return 2;
            })).then(literal("all").executes(all -> {
                try {
                    generateAll(tokenGenerateSize);
                } catch (Exception e) {
                    all.getSource().sendError(new TranslatableText("token.regenerate.failed.format"));
                }
                updateModMdoVariables();

                return 1;
            }))).then(literal("size").then(literal("128").executes(setSize128 -> {
                setGenerateSize(128);

                setSize128.getSource().sendFeedback(formatSetTokenSize(), false);

                return 128;
            })).then(literal("256").executes(setSize256 -> {
                setGenerateSize(256);

                setSize256.getSource().sendFeedback(formatSetTokenSize(), false);

                return 256;
            })).then(literal("512").executes(setSize512 -> {
                setGenerateSize(512);

                setSize512.getSource().sendFeedback(formatSetTokenSize(), false);

                return 512;
            })).then(literal("1024").executes(setSize1024 -> {
                setGenerateSize(1024);

                setSize1024.getSource().sendFeedback(formatSetTokenSize(), false);

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
