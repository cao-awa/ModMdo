package com.github.zhuaidadaya.modmdo.server.login;

import com.github.zhuaidadaya.modmdo.utils.usr.*;
import com.github.zhuaidadaya.modmdo.whitelist.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import java.util.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

public class ServerLogin {
    public void login(String name, String uuid, String identifier, int modmdoVersion) {
        login(name, uuid, identifier, String.valueOf(modmdoVersion));
    }

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
                    whitelist.put(name, new PermanentWhitelist(name, identifier, UUID.fromString(uuid)));
                    updateModMdoVariables();
                }
            }
            temporaryWhitelist.remove(name);
        });
        try {
            loginUsers.getUser(uuid).setIdentifier(identifier).setVersion(version);
        } catch (Exception e) {
            if (whitelist.getFromId(identifier) == null) {
                rejectUsers.put(new User(name, uuid, - 1, identifier, version));
            } else {
                LOGGER.info("login player using id login: " + name);
                loginUsers.put(new User(name, uuid, - 1, identifier, version));
            }
        }
    }

    public void strictLogin(String name, String uuid, String identifier, int version) {
        EntrustExecution.notNull(temporaryWhitelist.get(name), e -> {
            if (e.isValid()) {
                if (EntrustParser.trying(() -> ! whitelist.get(name).getIdentifier().equals(identifier), () -> true)) {
                    whitelist.put(name, new PermanentWhitelist(name, identifier, UUID.fromString(uuid)));
                    updateModMdoVariables();
                }
            }
            temporaryWhitelist.remove(name);
        });
        try {
            loginUsers.getUser(uuid).setIdentifier(identifier).setVersion(version);
        } catch (Exception e) {
            if (EntrustParser.trying(() -> ! whitelist.get(name).getIdentifier().equals(identifier), () -> true)) {
                reject(name, uuid, identifier, null);
            } else {
                LOGGER.info("login player using strict login: " + name);
                loginUsers.put(new User(name, uuid, - 1, identifier, version));
            }
        }
    }

    public void reject(String name, String uuid, String identifier, Text reson) {
        rejectUsers.put(new User(name, uuid, - 1, identifier, - 1).setRejectReason(reson));
    }

    public void loginUsingYgg(String name, String uuid) {
        EntrustExecution.notNull(temporaryWhitelist.get(name), e -> {
            if (e.isValid()) {
                try {
                    if (uuid.equals(whitelist.get(name).getRecorde().uuid().toString())) {
                        return;
                    }
                } catch (Exception ex) {

                }
                whitelist.put(name, new PermanentWhitelist(name, "", UUID.fromString(uuid)));
                updateModMdoVariables();
            }
            temporaryWhitelist.remove(name);
        });
        if (! uuid.equals(whitelist.get(name).getRecorde().uuid().toString())) {
            rejectUsers.put(new User(name, uuid, - 1, "", 0));
        } else {
            LOGGER.info("login player using ygg login: " + name);
            loginUsers.put(new User(name, uuid, - 1, "", 0));
        }
    }

    public void logout(ServerPlayerEntity player) {
        try {
            loginUsers.removeUser(player);
        } catch (Exception e) {

        }
    }
}