package com.github.cao.awa.modmdo.server.login;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.security.certificate.identity.*;
import com.github.cao.awa.modmdo.server.login.exception.*;
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

    public void login(@NotNull String name, @NotNull String uuid, @NotNull String identifier, String modmdoName, String unidirectionalVerify, String verifyKey) {
        if (config.getBoolean("modmdo_whitelist")) {
            if (config.getBoolean("whitelist_only_id")) {
                loginUsingSimple(
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
            LOGGER.info(
                    "Login player: {}",
                    name
            );
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

    public void loginUsingSimple(String name, String uuid, String identifier, String unidirectionalVerify, String verifyKey) {
        String idSha = calculateIdSha(identifier);
        Receptacle<Translatable> message = new Receptacle<>(null);
        EntrustEnvironment.notNull(
                temporaryStation.get(name),
                certificate -> {
                    if (certificate.isValid()) {
                        switch (certificate.getType()) {
                            case "whitelist" -> {
                                if (whitelists.getFromId(idSha) == null) {
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
        User user = new User(
                name,
                uuid,
                - 1,
                idSha
        );
        if (unidirectionalVerify != null && EntrustEnvironment.get(
                () -> {
                    if (temporaryInvites.containsName(name)) {
                        return false;
                    }
                    Certificate wl = whitelists.getFromId(idSha);
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
            reject(
                    user,
                    "SimpleModMdo"
            );
        } else {
            accept(
                    user.setMessage(message.get() == null ?
                                    null :
                                    message.get()
                                           .text()),
                    "SimpleModMdo"
            );
        }
    }

    public static void processInvite(String name, TemporaryCertificate certificate, Receptacle<Translatable> message) {
        if (temporaryInvites.get(name) == null) {
            temporaryInvites.put(
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

    public static @NotNull String calculateIdSha(@NotNull String identifier) {
        return EntrustEnvironment.get(
                () -> MessageDigger.digest(
                        identifier,
                        MessageDigger.Sha3.SHA_512
                ),
                identifier
        );
    }

    public void accept(User user, String type) {
        LOGGER.info(
                "Player '{}' success to login, using '{}'",
                user.getName(),
                type
        );
        loginUsers.put(user);
    }

    public static void acceptWhitelist(@NotNull String name, @NotNull String uuid, @Nullable String idSha, @Nullable String unidirectionalVerify) {
        whitelists.put(
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

    public void reject(User user, String type) {
        LOGGER.info(
                "Player '{}' failed to login, using '{}'",
                user.getName(),
                type
        );
        rejectUsers.put(user);
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
                                        () -> whitelists.get(name)
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

        User user = new User(
                name,
                uuid,
                - 1,
                idSha
        );
        if (EntrustEnvironment.get(
                () -> {
                    Certificate wl = whitelists.get(name);
                    boolean reject = wl.getIdentifier()
                                       .equals(idSha) && ! temporaryInvites.containsName(name);
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
            reject(
                    user,
                    "StrictModMdo"
            );
        } else {
            accept(
                    user.setMessage(message.get() == null ?
                                    null :
                                    message.get()
                                           .text()),
                    "StrictModMdo"
            );
        }
    }

    public void reject(String name, String uuid, String identifier, Text reson, String type) {
        reject(
                new User(
                        name,
                        uuid,
                        - 1,
                        identifier
                ).setMessage(reson),
                type
        );
    }

    public boolean loginUsingYgg(String name, String uuid) throws LoginFailedException {
        Receptacle<Translatable> message = Receptacle.of();
        EntrustEnvironment.notNull(
                temporaryStation.get(name),
                certificate -> {
                    if (certificate.isValid()) {
                        switch (certificate.getType()) {
                            case "whitelist" -> {
                                if (EntrustEnvironment.get(
                                        () -> uuid.equals(whitelists.get(name)
                                                                    .getRecorde()
                                                                    .getUuid()
                                                                    .toString()),

                                        false
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
        if (! temporaryInvites.containsName(name) && ! whitelists.verifyUUID(
                name,
                uuid
        )) {
            throw new LoginFailedException();
        }
        accept(
                user.setMessage(message.get() == null ?
                                null :
                                message.get()
                                       .text()),
                "Ygg"
        );
        return true;
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
                    if (temporaryInvites.containsName(EntityUtil.getName(player))) {
                        LOGGER.info(
                                "Invite expired for player: {}",
                                EntityUtil.getName(player)
                        );
                        temporaryInvites.remove(EntityUtil.getName(player));
                    }
                });
            }
        });
    }
}
