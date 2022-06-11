package com.github.cao.awa.modmdo.server.login;

import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.cao.awa.modmdo.certificate.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.getLanguage;
import static com.github.cao.awa.modmdo.storage.SharedVariables.tracker;

public class ServerLogin {
    public void login(String name, String uuid, String identifier, String modmdoVersion) {
        login(name, uuid, identifier, modmdoVersion, null);
    }

    public void login(String name, String uuid, String identifier, String modmdoVersion, String language) {
        int version = EntrustParser.tryCreate(() -> Integer.valueOf(modmdoVersion), - 1);

        if (SharedVariables.config.getConfigBoolean("modmdo_whitelist")) {
            if (SharedVariables.config.getConfigBoolean("whitelist_only_id")) {
                loginUsingId(name, uuid, identifier, version);
            } else {
                strictLogin(name, uuid, identifier, version);
            }
        } else {
            EntrustExecution.tryTemporary(() -> {
                tracker.submit("Login player: " + name);
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
                tracker.submit("Login player using id login: " + name);
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
                tracker.submit("Login player using strict login: " + name);
                SharedVariables.loginUsers.put(new User(name, uuid, - 1, identifier, version));
            }
        }
    }

    public void suffix(User user, String suffix) {
        user.setSuffix(suffix);
    }

    public void reject(String name, String uuid, String identifier, Text reson) {
        SharedVariables.rejectUsers.put(new User(name, uuid, - 1, identifier, - 1).setRejectReason(reson));
    }

    public void loginUsingYgg(String name, String uuid) {
        EntrustExecution.notNull(SharedVariables.temporaryWhitelist.get(name), e -> {
            if (e.isValid()) {
                if (EntrustParser.trying(() -> uuid.equals(SharedVariables.whitelist.get(name).getRecorde().uuid().toString()), ex -> false)) {
                    return;
                }
                SharedVariables.whitelist.put(name, new PermanentCertificate(name, "", UUID.fromString(uuid)));
                SharedVariables.saveVariables();
            }
            SharedVariables.temporaryWhitelist.remove(name);
        });
        if (! uuid.equals(SharedVariables.whitelist.get(name).getRecorde().uuid().toString())) {
            SharedVariables.rejectUsers.put(new User(name, uuid, - 1, "", 0));
        } else {
            tracker.submit("Login player using ygg login: " + name);
            SharedVariables.loginUsers.put(new User(name, uuid, - 1, "", 0));
        }
    }

    public void logout(ServerPlayerEntity player) {
        tracker.submit("Logout player: " + player.getName().asString());
        EntrustExecution.tryTemporary(() -> {
            SharedVariables.loginUsers.removeUser(player);
        });
    }
}
