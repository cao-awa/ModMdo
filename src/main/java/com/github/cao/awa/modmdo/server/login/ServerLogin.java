package com.github.cao.awa.modmdo.server.login;

import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.cao.awa.modmdo.whitelist.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.getLanguage;

public class ServerLogin {
    public void login(String name, String uuid, String identifier, int modmdoVersion) {
        login(name, uuid, identifier, String.valueOf(modmdoVersion), null);
    }

    public void login(String name, String uuid, String identifier, String modmdoVersion, String language) {
        int version = EntrustParser.tryCreate(() -> Integer.valueOf(modmdoVersion), - 1);

        System.out.println(identifier);

        if (SharedVariables.config.getConfigBoolean("modmdo_whitelist")) {
            if (SharedVariables.config.getConfigBoolean("whitelist_only_id")) {
                loginUsingId(name, uuid, identifier, version);
            } else {
                strictLogin(name, uuid, identifier, version);
            }
        } else {
            EntrustExecution.tryTemporary(() -> {
                SharedVariables.LOGGER.info("login player: " + name);
                SharedVariables.loginUsers.put(new User(name, uuid, - 1, identifier, version).setLanguage(language == null ? getLanguage() : Language.ofs(language)));
            });
        }
    }

    public void loginUsingId(String name, String uuid, String identifier, int version) {
        EntrustExecution.notNull(SharedVariables.temporaryWhitelist.get(name), e -> {
            if (e.isValid()) {
                if (SharedVariables.whitelist.getFromId(identifier) == null) {
                    SharedVariables.whitelist.put(name, new PermanentCertificate(name, identifier, UUID.fromString(uuid)));
                    SharedVariables.saveVariables();
                }
            }
            SharedVariables.temporaryWhitelist.remove(name);
        });
        try {
            SharedVariables.loginUsers.getUser(uuid).setIdentifier(identifier).setVersion(version);
        } catch (Exception e) {
            if (SharedVariables.whitelist.getFromId(identifier) == null) {
                SharedVariables.rejectUsers.put(new User(name, uuid, - 1, identifier, version));
            } else {
                SharedVariables.LOGGER.info("login player using id login: " + name);
                SharedVariables.loginUsers.put(new User(name, uuid, - 1, identifier, version));
            }
        }
    }

    public void strictLogin(String name, String uuid, String identifier, int version) {
        EntrustExecution.notNull(SharedVariables.temporaryWhitelist.get(name), e -> {
            if (e.isValid()) {
                if (EntrustParser.trying(() -> ! SharedVariables.whitelist.get(name).getIdentifier().equals(identifier), () -> true)) {
                    SharedVariables.whitelist.put(name, new PermanentCertificate(name, identifier, UUID.fromString(uuid)));
                    SharedVariables.saveVariables();
                }
            }
            SharedVariables.temporaryWhitelist.remove(name);
        });
        try {
            SharedVariables.loginUsers.getUser(uuid).setIdentifier(identifier).setVersion(version);
        } catch (Exception e) {
            if (EntrustParser.trying(() -> ! SharedVariables.whitelist.get(name).getIdentifier().equals(identifier), () -> true)) {
                reject(name, uuid, identifier, null);
            } else {
                SharedVariables.LOGGER.info("login player using strict login: " + name);
                SharedVariables.loginUsers.put(new User(name, uuid, - 1, identifier, version));
            }
        }
    }

    public void reject(String name, String uuid, String identifier, Text reson) {
        SharedVariables.rejectUsers.put(new User(name, uuid, - 1, identifier, - 1).setRejectReason(reson));
    }

    public void loginUsingYgg(String name, String uuid) {
        EntrustExecution.notNull(SharedVariables.temporaryWhitelist.get(name), e -> {
            if (e.isValid()) {
                try {
                    if (uuid.equals(SharedVariables.whitelist.get(name).getRecorde().uuid().toString())) {
                        return;
                    }
                } catch (Exception ex) {

                }
                SharedVariables.whitelist.put(name, new PermanentCertificate(name, "", UUID.fromString(uuid)));
                SharedVariables.saveVariables();
            }
            SharedVariables.temporaryWhitelist.remove(name);
        });
        if (! uuid.equals(SharedVariables.whitelist.get(name).getRecorde().uuid().toString())) {
            SharedVariables.rejectUsers.put(new User(name, uuid, - 1, "", 0));
        } else {
            SharedVariables.LOGGER.info("login player using ygg login: " + name);
            SharedVariables.loginUsers.put(new User(name, uuid, - 1, "", 0));
        }
    }

    public void logout(ServerPlayerEntity player) {
        try {
            SharedVariables.loginUsers.removeUser(player);
        } catch (Exception e) {

        }
    }
}
