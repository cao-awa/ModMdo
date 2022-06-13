package com.github.cao.awa.modmdo.server.login;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

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
                TRACKER.info("Login player: " + name);
                SharedVariables.loginUsers.put(new User(name, uuid, - 1, identifier, version).setLanguage(language == null ? getLanguage() : Language.ofs(language)));
            });
        }
    }

    public void loginUsingId(String name, String uuid, String identifier, int version) {
        Receptacle<Translatable> message = new Receptacle<>(null);
        EntrustExecution.notNull(SharedVariables.temporaryStation.get(name), e -> {
            if (e.isValid()) {
                switch (e.getType()) {
                    case "whitelist" -> {
                        if (SharedVariables.whitelist.getFromId(identifier) == null) {
                            SharedVariables.whitelist.put(name, new PermanentCertificate(name, identifier, UUID.fromString(uuid)));
                            SharedVariables.saveVariables();
                        }
                    }
                    case "invite" -> {
                        if (SharedVariables.temporaryInvite.get(name) == null) {
                            SharedVariables.temporaryInvite.put(name, e.getSpare());
                            EntrustExecution.notNull(e.getPass(), pass -> {
                                message.set(TextUtil.translatable("modmdo.invite.using", pass.getOrganizer(), pass.formatRemaining()));
                            });
                        }
                    }
                }
            }
            SharedVariables.temporaryStation.remove(name);
        });
        try {
            SharedVariables.loginUsers.getUser(uuid).setIdentifier(identifier).setVersion(version);
        } catch (Exception e) {
            if (SharedVariables.whitelist.getFromId(identifier) == null || ! temporaryInvite.containsName(name)) {
                SharedVariables.rejectUsers.put(new User(name, uuid, - 1, identifier, version));
            } else {
                TRACKER.info("Login player using id login: " + name);
                SharedVariables.loginUsers.put(new User(name, uuid, - 1, identifier, version).setMessage(message.get() == null ? null : message.get().text()));
            }
        }
    }

    public void strictLogin(String name, String uuid, String identifier, int version) {
        Receptacle<Translatable> message = new Receptacle<>(null);
        EntrustExecution.notNull(SharedVariables.temporaryStation.get(name), e -> {
            if (e.isValid()) {
                switch (e.getType()) {
                    case "whitelist" -> {
                        if (EntrustParser.trying(() -> ! SharedVariables.whitelist.get(name).getIdentifier().equals(identifier), () -> true)) {
                            SharedVariables.whitelist.put(name, new PermanentCertificate(name, identifier, UUID.fromString(uuid)));
                            SharedVariables.saveVariables();
                        }
                    }
                    case "invite" -> {
                        if (SharedVariables.temporaryInvite.get(name) == null) {
                            SharedVariables.temporaryInvite.put(name, e.getSpare());
                            System.out.println(e.getPass());
                            EntrustExecution.notNull(e.getPass(), pass -> {
                                message.set(TextUtil.translatable("modmdo.invite.using", pass.getOrganizer(), pass.formatRemaining()));
                            });
                        }
                    }
                }
            }
            SharedVariables.temporaryStation.remove(name);
        });
        try {
            SharedVariables.loginUsers.getUser(uuid).setIdentifier(identifier).setVersion(version);
        } catch (Exception e) {
            if (EntrustParser.trying(() -> ! SharedVariables.whitelist.get(name).getIdentifier().equals(identifier), () -> ! temporaryInvite.containsName(name))) {
                reject(name, uuid, identifier, null);
            } else {
                TRACKER.info("Login player using strict login: " + name);
                SharedVariables.loginUsers.put(new User(name, uuid, - 1, identifier, version).setMessage(message.get() == null ? null : message.get().text()));
            }
        }
    }

    public void reject(String name, String uuid, String identifier, Text reson) {
        SharedVariables.rejectUsers.put(new User(name, uuid, - 1, identifier, - 1).setMessage(reson));
    }

    public void suffix(User user, String suffix) {
        user.setSuffix(suffix);
    }

    public void loginUsingYgg(String name, String uuid) {
        Receptacle<Translatable> message = new Receptacle<>(null);
        EntrustExecution.notNull(SharedVariables.temporaryStation.get(name), e -> {
            if (e.isValid()) {
                switch (e.getType()) {
                    case "whitelist" -> {
                        if (EntrustParser.trying(() -> uuid.equals(SharedVariables.whitelist.get(name).getRecorde().uuid().toString()), ex -> false)) {
                            return;
                        }
                        SharedVariables.whitelist.put(name, new PermanentCertificate(name, "", UUID.fromString(uuid)));
                        SharedVariables.saveVariables();
                    }
                    case "invite" -> {
                        if (SharedVariables.temporaryInvite.get(name) == null) {
                            SharedVariables.temporaryInvite.put(name, e.getSpare());
                            EntrustExecution.notNull(e.getPass(), pass -> {
                                message.set(TextUtil.translatable("modmdo.invite.using", pass.getOrganizer(), pass.formatRemaining()));
                            });
                        }
                    }
                }
            }
            SharedVariables.temporaryStation.remove(name);
        });
        if (! uuid.equals(SharedVariables.whitelist.get(name).getRecorde().uuid().toString()) && ! temporaryInvite.containsName(name)) {
            SharedVariables.rejectUsers.put(new User(name, uuid, - 1, "", 0));
        } else {
            TRACKER.info("Login player using ygg login: " + name);
            SharedVariables.loginUsers.put(new User(name, uuid, - 1, "", 0).setMessage(message.get() == null ? null : message.get().text()));
        }
    }

    public void logout(ServerPlayerEntity player) {
        TRACKER.info("Logout player: " + EntityUtil.getName(player));
        EntrustExecution.tryTemporary(() -> {
            SharedVariables.loginUsers.removeUser(player);
            if (temporaryInvite.containsName(EntityUtil.getName(player))) {
                TRACKER.info("Invite expired for player: " + EntityUtil.getName(player));
                temporaryInvite.remove(EntityUtil.getName(player));
            }
        });
    }
}
