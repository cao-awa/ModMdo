package com.github.cao.awa.modmdo.server.login;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.encryption.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.json.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Server
public class ServerLogin {
    public void login(String name, String uuid, String identifier, String modmdoVersion, String unidirectionalVerify, String verifyKey) {
        login(name, uuid, identifier, modmdoVersion, null, unidirectionalVerify, verifyKey);
    }

    public void login(String name, String uuid, String identifier, String modmdoVersion, String modmdoName, String unidirectionalVerify, String verifyKey) {
        int version = EntrustParser.tryCreate(() -> Integer.valueOf(modmdoVersion), - 1);

        if (SharedVariables.config.getConfigBoolean("modmdo_whitelist")) {
            if (SharedVariables.config.getConfigBoolean("whitelist_only_id")) {
                loginUsingId(name, uuid, identifier, version, unidirectionalVerify, verifyKey);
            } else {
                strictLogin(name, uuid, identifier, version, unidirectionalVerify, verifyKey);
            }
        } else {
            EntrustExecution.tryTemporary(() -> {
                TRACKER.info("Login player: " + name);
                SharedVariables.loginUsers.put(new User(name, uuid, - 1, identifier, version).setModmdoName(modmdoName));
            });
        }
        if (loginUsers.hasUser(uuid)) {
            loginUsers.getUser(uuid).setModmdoName(modmdoName);
        }
    }

    public void loginUsingId(String name, String uuid, String identifier, int version, String unidirectionalVerify, String verifyKey) {
        Receptacle<Translatable> message = new Receptacle<>(null);
        EntrustExecution.notNull(SharedVariables.temporaryStation.get(name), e -> {
            if (e.isValid()) {
                switch (e.getType()) {
                    case "whitelist" -> {
                        if (SharedVariables.whitelist.getFromId(identifier) == null) {
                            SharedVariables.whitelist.put(name, new PermanentCertificate(name, identifier, UUID.fromString(uuid), unidirectionalVerify));
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
            if (EntrustParser.trying(() -> {
                boolean reject = true;
                Certificate wl = SharedVariables.whitelist.getFromId(identifier);
                boolean invite = temporaryInvite.containsName(name);
                if (invite) {
                    return false;
                }
                if (wl != null) {
                    reject = false;
                    if (unidirectionalVerify != null) {
                        if (wl.getRecorde().getIdentity().getVerify().equals("")) {
                            SharedVariables.saveVariables(() -> {
                                wl.getRecorde().getIdentity().setVerify(unidirectionalVerify);
                                wl.getRecorde().getIdentity().setUniqueId(identifier);
                            });
                            return false;
                        }
                        if (wl.getRecorde().getIdentity().getVerify().equals(unidirectionalVerify)) {
                            String id = new JSONObject(AES.aesDecryptToString(wl.getRecorde().getIdentity().getVerify().getBytes(), verifyKey.getBytes())).getString("identifier");
                            reject = ! id.equals(identifier);
                        } else {
                            reject = true;
                        }
                    }
                }
                return reject;
            }, ex -> {
                ex.printStackTrace();
                TRACKER.submit("Exception in checking login", ex);
                return true;
            })) {
                TRACKER.info("Reject player using id login: " + name);
                SharedVariables.rejectUsers.put(new User(name, uuid, - 1, identifier, version));
            } else {
                TRACKER.info("Login player using id login: " + name);
                SharedVariables.loginUsers.put(new User(name, uuid, - 1, identifier, version).setMessage(message.get() == null ? null : message.get().text()));
            }
        }
    }

    public void strictLogin(String name, String uuid, String identifier, int version, String unidirectionalVerify, String verifyKey) {
        Receptacle<Translatable> message = new Receptacle<>(null);
        EntrustExecution.notNull(SharedVariables.temporaryStation.get(name), e -> {
            if (e.isValid()) {
                switch (e.getType()) {
                    case "whitelist" -> {
                        if (EntrustParser.trying(() -> ! SharedVariables.whitelist.get(name).getIdentifier().equals(identifier), () -> true)) {
                            SharedVariables.whitelist.put(name, new PermanentCertificate(name, identifier, UUID.fromString(uuid), unidirectionalVerify));
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
            if (EntrustParser.tryCreate(() -> {
                Certificate wl = SharedVariables.whitelist.get(name);
                boolean reject = wl.getIdentifier().equals(identifier) && ! temporaryInvite.containsName(name);
                if (unidirectionalVerify != null) {
                    if (wl.getRecorde().getIdentity().getVerify().equals("")) {
                        SharedVariables.saveVariables(() -> {
                            wl.getRecorde().getIdentity().setVerify(unidirectionalVerify);
                            wl.getRecorde().getIdentity().setUniqueId(identifier);
                        });
                    }
                    if (wl.getRecorde().getIdentity().getVerify().equals(unidirectionalVerify)) {
                        String id = new JSONObject(AES.aesDecryptToString(wl.getRecorde().getIdentity().getVerify().getBytes(), verifyKey.getBytes())).getString("identifier");
                        reject = ! id.equals(identifier);
                    } else {
                        reject = true;
                    }
                }
                return reject;
            }, true)) {
                TRACKER.info("Reject player using strict login: " + name);
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

    public void loginUsingYgg(String name, String uuid) {
        Receptacle<Translatable> message = new Receptacle<>(null);
        EntrustExecution.notNull(SharedVariables.temporaryStation.get(name), e -> {
            if (e.isValid()) {
                switch (e.getType()) {
                    case "whitelist" -> {
                        if (EntrustParser.trying(() -> uuid.equals(SharedVariables.whitelist.get(name).getRecorde().getUuid().toString()), ex -> false)) {
                            return;
                        }
                        SharedVariables.whitelist.put(name, new PermanentCertificate(name, "", UUID.fromString(uuid), null));
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
        if (! uuid.equals(SharedVariables.whitelist.get(name).getRecorde().getUuid().toString()) && ! temporaryInvite.containsName(name)) {
            TRACKER.info("Reject player using ygg login: " + name);
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
