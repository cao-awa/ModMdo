package com.github.cao.awa.modmdo.server.login;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.security.certificate.identity.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.digger.*;
import com.github.cao.awa.modmdo.utils.encryption.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;
import org.json.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Server
public class ServerLogin {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoLoginChecker");

    public void login(String name, String uuid, String identifier, String unidirectionalVerify, String verifyKey) {
        login(
                name,
                uuid,
                identifier,
                null,
                unidirectionalVerify,
                verifyKey
        );
    }

    public void login(String name, String uuid, String identifier, String modmdoName, String unidirectionalVerify, String verifyKey) {
        if (config.getBoolean("modmdo_whitelist")) {
            if (config.getBoolean("whitelist_only_id")) {
                loginUsingId(
                        name,
                        uuid,
                        identifier,
                        unidirectionalVerify,
                        verifyKey
                );
            } else {
                strictLogin(
                        name,
                        uuid,
                        identifier,
                        unidirectionalVerify,
                        verifyKey
                );
            }
        } else {
            String idSha = calculateIdSha(identifier);
            LOGGER.info("Login player: {}",
                        name);
            EntrustEnvironment.trys(
                    () -> loginUsers.getUser(uuid)
                                    .setIdentifier(idSha)
                                    .setLogged(false),
                    () -> loginUsers.put(new User(
                            name,
                            uuid,
                            - 1,
                            idSha
                    ).setLogged(false))
            );
        }
        if (loginUsers.hasUser(uuid)) {
            loginUsers.getUser(uuid)
                      .setModmdoName(modmdoName);
        }
    }

    public void loginUsingId(String name, String uuid, String identifier, String unidirectionalVerify, String verifyKey) {
        String idSha = calculateIdSha(identifier);
        Receptacle<Translatable> message = new Receptacle<>(null);
        EntrustEnvironment.notNull(
                temporaryStation.get(name),
                certificate -> {
                    if (certificate.isValid()) {
                        switch (certificate.getType()) {
                            case "whitelist" -> {
                                if (whitelist.getFromId(idSha) == null) {
                                    acceptWhitelist(
                                            name,
                                            uuid,
                                            idSha,
                                            unidirectionalVerify
                                    );
                                }
                            }
                            case "invite" -> processInvite(
                                    name,
                                    certificate,
                                    message
                            );
                        }
                    }
                    temporaryStation.remove(name);
                }
        );
        EntrustEnvironment.trys(
                () -> loginUsers.getUser(uuid)
                                .setIdentifier(identifier),
                () -> {
                    User user = new User(
                            name,
                            uuid,
                            - 1,
                            idSha
                    );
                    if (unidirectionalVerify != null && EntrustEnvironment.get(
                            () -> {
                                if (temporaryInvite.containsName(name)) {
                                    return false;
                                }
                                Certificate wl = whitelist.getFromId(idSha);
                                if (wl == null) {
                                    return true;
                                }
                                Identity identity = wl.getRecorde()
                                                      .getIdentity();

                                return ! verify(
                                        identity,
                                        identity.getVerify(),
                                        unidirectionalVerify,
                                        verifyKey,
                                        idSha
                                );
                            },
                            true
                    )) {
                        LOGGER.info(
                                "Reject player using id login: {}",
                                name
                        );
                        reject(user);
                    } else {
                        LOGGER.info(
                                "Login player using id login: {}",
                                name
                        );
                        accept(user.setMessage(message.get() == null ?
                                               null :
                                               message.get()
                                                      .text()));
                    }
                }
        );
    }

    public static void processInvite(String name, TemporaryCertificate certificate, Receptacle<Translatable> message) {
        if (temporaryInvite.get(name) == null) {
            temporaryInvite.put(
                    name,
                    certificate.snapSpare()
            );
            EntrustEnvironment.notNull(
                    certificate.getPass(),
                    pass -> message.set(TextUtil.translatable(
                            "modmdo.invite.using",
                            pass.getOrganizer(),
                            pass.formatRemaining()
                    ))
            );
        }
    }

    public static boolean verify(Identity identity, String verify, String unidirectionalVerify, String verifyKey, String idSha) throws Exception {
        if (verify.equals("")) {
            saveVariables(() -> {
                identity.setVerify(unidirectionalVerify);
                identity.setUniqueId(idSha);
            });
            return false;
        }
        return verify.equals(unidirectionalVerify) || ! new JSONObject(AES.aesDecryptToString(
                verify.getBytes(),
                verifyKey.getBytes()
        )).getString("identifier")
          .equals(idSha);
    }

    public static String calculateIdSha(String identifier) {
        return EntrustEnvironment.get(
                () -> MessageDigger.digest(
                        identifier,
                        MessageDigger.Sha3.SHA_512
                ),
                identifier
        );
    }

    public void reject(User user) {
        rejectUsers.put(user);
    }

    public void accept(User user) {
        loginUsers.put(user);
    }

    public static void acceptWhitelist(@NotNull String name, @NotNull String uuid, @Nullable String idSha, @Nullable String unidirectionalVerify) {
        whitelist.put(
                name,
                new PermanentCertificate(
                        name,
                        idSha,
                        UUID.fromString(uuid),
                        unidirectionalVerify
                )
        );
        saveVariables();
    }

    public void strictLogin(String name, String uuid, String identifier, String unidirectionalVerify, String verifyKey) {
        Receptacle<Translatable> message = new Receptacle<>(null);
        String idSha = calculateIdSha(identifier);
        EntrustEnvironment.notNull(
                temporaryStation.get(name),
                certificate -> {
                    if (certificate.isValid()) {
                        switch (certificate.getType()) {
                            case "whitelist" -> {
                                if (EntrustEnvironment.trys(
                                        () -> whitelist.get(name)
                                                       .getIdentifier()
                                                       .equals(idSha),
                                        () -> false
                                )) {
                                    return;
                                }
                                acceptWhitelist(
                                        name,
                                        uuid,
                                        idSha,
                                        unidirectionalVerify
                                );
                            }
                            case "invite" -> processInvite(
                                    name,
                                    certificate,
                                    message
                            );
                        }
                    }
                    temporaryStation.remove(name);
                }
        );
        EntrustEnvironment.trys(
                () -> loginUsers.getUser(uuid)
                                .setIdentifier(idSha),
                () -> {
                    User user = new User(
                            name,
                            uuid,
                            - 1,
                            idSha
                    );
                    if (EntrustEnvironment.get(
                            () -> {
                                Certificate wl = whitelist.get(name);
                                boolean reject = wl.getIdentifier()
                                                   .equals(idSha) && ! temporaryInvite.containsName(name);
                                if (unidirectionalVerify == null) {
                                    return reject;
                                }
                                Identity identity = wl.getRecorde()
                                                      .getIdentity();

                                return ! verify(
                                        identity,
                                        identity.getVerify(),
                                        unidirectionalVerify,
                                        verifyKey,
                                        idSha
                                );
                            },
                            true
                    )) {
                        LOGGER.info(
                                "Reject player using strict login: {}",
                                name
                        );
                        reject(user);
                    } else {
                        LOGGER.info(
                                "Login player using strict login: {}",
                                name
                        );
                        accept(user.setMessage(message.get() == null ?
                                               null :
                                               message.get()
                                                      .text()));
                    }
                }
        );
    }

    public void reject(String name, String uuid, String identifier, Text reson) {
        reject(new User(
                name,
                uuid,
                - 1,
                identifier
        ).setMessage(reson));
    }

    public void loginUsingYgg(String name, String uuid) {
        Receptacle<Translatable> message = Receptacle.of();
        EntrustEnvironment.notNull(
                temporaryStation.get(name),
                certificate -> {
                    if (certificate.isValid()) {
                        switch (certificate.getType()) {
                            case "whitelist" -> {
                                if (EntrustEnvironment.trys(
                                        () -> uuid.equals(whitelist.get(name)
                                                                   .getRecorde()
                                                                   .getUuid()
                                                                   .toString()),

                                        ex -> false
                                )) {
                                    return;
                                }
                                acceptWhitelist(
                                        name,
                                        uuid,
                                        null,
                                        null
                                );
                            }
                            case "invite" -> processInvite(
                                    name,
                                    certificate,
                                    message
                            );
                        }
                    }
                    temporaryStation.remove(name);
                }
        );

        User user = new User(
                name,
                uuid,
                - 1,
                ""
        );
        if (! temporaryInvite.containsName(name) && ! uuid.equals(whitelist.get(name)
                                                                           .getRecorde()
                                                                           .getUuid()
                                                                           .toString())) {
            LOGGER.info(
                    "Reject player using ygg login: {}",
                    name
            );
            reject(user);
        } else {
            LOGGER.info(
                    "Login player using ygg login: {}",
                    name
            );
            accept(user.setMessage(message.get() == null ?
                                   null :
                                   message.get()
                                          .text()));
        }
    }

    public void logout(ServerPlayerEntity player) {
        EntrustEnvironment.trys(() -> {
            if (Objects.requireNonNull(player.getServer())
                       .getPlayerManager()
                       .getPlayerList()
                       .contains(player)) {
                LOGGER.info(
                        "Logout player: {}",
                        EntityUtil.getName(player)
                );
                EntrustEnvironment.trys(() -> {
                    loginUsers.removeUser(player);
                    if (temporaryInvite.containsName(EntityUtil.getName(player))) {
                        LOGGER.info(
                                "Invite expired for player: {}",
                                EntityUtil.getName(player)
                        );
                        temporaryInvite.remove(EntityUtil.getName(player));
                    }
                });
            }
        });
    }
}
