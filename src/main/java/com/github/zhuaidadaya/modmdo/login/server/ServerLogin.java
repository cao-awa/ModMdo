package com.github.zhuaidadaya.modmdo.login.server;

import com.github.zhuaidadaya.modmdo.utils.usr.User;
import com.github.zhuaidadaya.modmdo.whitelist.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ServerLogin {
    public void login(String name, String uuid, String identifier, String modmdoVersion) {
        int version = EntrustParser.tryCreate(() -> Integer.valueOf(modmdoVersion), - 1);

        if (! identifier.equals("")) {
            if (config.getConfigBoolean("modmdo_whitelist")) {
                if (config.getConfigBoolean("whitelist_only_id")) {
                    loginUsingId(name, uuid, identifier, version);
                } else {
                    strictLogin(name, uuid, identifier, version);
                }
            } else {
                EntrustExecution.tryTemporary(() -> {
                    LOGGER.info("login player: " + name);
                    loginUsers.put(new User(name, uuid, - 1, identifier, version));
                });
            }
        }
    }

    public void loginUsingId(String name, String uuid, String identifier, int version) {
        EntrustExecution.notNull(temporaryWhitelist.get(name), e -> {
            if (e.isValid()) {
                if (whitelist.getFromId(identifier) == null) {
                    whitelist.put(name, new PermanentWhitelist(name, identifier));
                    updateModMdoVariables();
                }
            }
            temporaryWhitelist.remove(name);
        });
        if (whitelist.getFromId(identifier) == null) {
            rejectUsers.put(new User(name, uuid, - 1, identifier, version));
        } else {
            LOGGER.info("login player: " + name);
            loginUsers.put(new User(name, uuid, - 1, identifier, version));
        }
    }

    public void strictLogin(String name, String uuid, String identifier, int version) {
        EntrustExecution.notNull(temporaryWhitelist.get(name), e -> {
            if (e.isValid()) {
                if (EntrustParser.trying(() -> ! whitelist.get(name).getIdentifier().equals(identifier), () -> true)) {
                    whitelist.put(name, new PermanentWhitelist(name, identifier));
                    updateModMdoVariables();
                }
            }
            temporaryWhitelist.remove(name);
        });
        if (EntrustParser.trying(() -> ! whitelist.get(name).getIdentifier().equals(identifier), () -> true)) {
            rejectUsers.put(new User(name, uuid, - 1, identifier, version));
        } else {
            LOGGER.info("login player: " + name);
            loginUsers.put(new User(name, uuid, - 1, identifier, version));
        }
    }

    public void logout(ServerPlayerEntity player) {
        try {
            loginUsers.removeUser(player);
        } catch (Exception e) {

        }
    }
}
