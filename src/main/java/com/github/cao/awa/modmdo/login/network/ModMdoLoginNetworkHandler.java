package com.github.cao.awa.modmdo.login.network;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.network.handle.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.entity.player.*;
import com.github.cao.awa.modmdo.utils.packet.buf.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoLoginNetworkHandler extends ServerPlayPacketHandler {
    private final Logger LOGGER = LogManager.getLogger("ModMdoLoginNetworkHandler");
    private final MinecraftServer server;
    private final ClientConnection connection;
    private final ServerPlayerEntity player;

    public ModMdoLoginNetworkHandler(MinecraftServer server, ClientConnection connection, ServerPlayerEntity player) {
        this.server = server;
        this.connection = connection;
        this.player = player;
        connection.setPacketListener(this);
    }

    @Override
    public void onCustomPayload(CustomPayloadC2SPacket packet) {
        try {
            if (serverUnderDdosAttack.get()) {
                return;
            }
            Identifier channel = EntrustEnvironment.get(
                    packet::getChannel,
                    new Identifier("")
            );

            PacketByteBuf packetByteBuf = EntrustEnvironment.trys(() -> new PacketByteBuf(packet.getData()
                                                                                                .copy()));

            EntrustEnvironment.notNull(
                    packetByteBuf,
                    buf -> {
                        PacketDataProcessor processor = new PacketDataProcessor(buf);
                        String oldLogin = "";
                        Identifier informationSign = new Identifier("");
                        if (TOKEN_CHANNEL.equals(channel)) {
                            oldLogin = processor.readString();
                        } else {
                            informationSign = processor.readIdentifier();
                        }
                        JSONObject loginData = processor.readJSONObject();

                        String name = loginData.getString("name");
                        String uuid = loginData.getString("uuid");
                        String identifier = loginData.getString("identifier");
                        String modmdoName = loginData.getString("versionName");
                        String unidirectionalVerify = loginData.getString("verifyData");
                        String verifyKey = loginData.getString("verifyKey");

                        if (TOKEN_CHANNEL.equals(channel)) {
                            LOGGER.debug("Processing client obsoleted login data");
                            serverLogin.reject(
                                    name,
                                    oldLogin,
                                    "",
                                    TextUtil.literal("Obsolete login type")
                                            .text(),
                                    "TokenAuth"
                            );
                            return;
                        }

                        if (CLIENT_CHANNEL.equals(channel)) {
                            LOGGER.debug("Processing client login data");
                            if (informationSign.equals(LOGIN_CHANNEL)) {
                                LOGGER.info("Name: {}",
                                             name);
                                LOGGER.info("UUID: {}",
                                             uuid);
                                LOGGER.info("Identifier: {}",
                                             identifier);
                                LOGGER.info("ModMdo Name: {}",
                                             modmdoName);
                                LOGGER.info("Verify Data: {}",
                                             unidirectionalVerify);
                                LOGGER.info("Verify Key: {}",
                                             verifyKey);

                                if (modMdoType == ModMdoType.SERVER) {
                                    if (beforeLogin()) {
                                        serverLogin.login(
                                                name,
                                                uuid,
                                                identifier,
                                                modmdoName,
                                                unidirectionalVerify,
                                                verifyKey
                                        );
                                        afterLogin();
                                    }
                                }
                            }
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean beforeLogin() {
        String name = EntityUtil.getName(player);
        if (server.getPlayerManager()
                  .getPlayer(name) != null) {
            return false;
        }
        if (loginUsers.hasUser(name)) {
            disconnect(Translatable.translatable("login.dump.rejected")
                                   .text());
            return false;
        }
        return true;

    }

    public void disconnect(Text reason) {
        this.connection.send(new DisconnectS2CPacket(reason));
        this.connection.disconnect(reason);
    }

    public void afterLogin() {
        if (modmdoWhitelist) {
            String name = EntityUtil.getName(player);
            if (rejectUsers.hasUser(player)) {
                User rejected = rejectUsers.getUser(player.getUuid());
                if (rejected.getMessage() == null) {
                    LOGGER.warn(
                            "ModMdo rejected player '{}' login, because player are not whitelisted",
                            name
                    );
                } else {
                    LOGGER.warn("ModMdo rejected player '{}' login",
                                name);
                }
                disconnect(rejected.getMessage() == null ?
                           TextUtil.translatable("multiplayer.disconnect.not_whitelisted")
                                   .text() :
                           rejected.getMessage());

                rejectUsers.removeUser(player);

                LOGGER.info("Rejected player: " + name);
                return;
            } else {
                if (loginTimedOut.containsKey(name)) {
                    if (loginTimedOut.get(name) < TimeUtil.millions()) {
                        disconnect(TextUtil.literal("Login timed out")
                                           .text());
                        LOGGER.warn(
                                "ModMdo rejected player '{}' login, because player not sent login request",
                                name
                        );

                        LOGGER.info(
                                "Rejected player: {}",
                                name
                        );
                        return;
                    }
                }
            }

            if (! connection.isOpen()) {
                return;
            }

            if (handleBanned(player)) {
                Certificate certificate = banned.get(EntityUtil.getName(player));
                if (certificate instanceof TemporaryCertificate temporary) {
                    String remaining = temporary.formatRemaining();
                    disconnect(minecraftTextFormat.format(
                                                          new com.github.cao.awa.modmdo.lang.Dictionary(certificate.getLastLanguage()),
                                                          "multiplayer.disconnect.banned-time-limited",
                                                          remaining
                                                  )
                                                  .text());
                    LOGGER.info(
                            "Player {} has been banned form server",
                            PlayerUtil.getName(player)
                    );
                } else {
                    disconnect(minecraftTextFormat.format(
                                                          new Dictionary(certificate.getLastLanguage()),
                                                          "multiplayer.disconnect.banned-indefinite"
                                                  )
                                                  .text());
                    LOGGER.info(
                            "Player {} has been banned form server",
                            PlayerUtil.getName(player)
                    );
                }
            } else {
                EntrustEnvironment.trys(
                        () -> {
                            if (connection.isOpen()) {
                                if (! loginUsers.hasUser(player)) {
                                    if (! config.getBoolean("modmdo_whitelist")) {
                                        serverLogin.login(
                                                player.getName()
                                                      .getString(),
                                                player.getUuid()
                                                      .toString(),
                                                "",
                                                "",
                                                null,
                                                null
                                        );
                                    } else {
                                        disconnect(Translatable.translatable("multiplayer.disconnect.not_whitelisted")
                                                               .text());
                                    }
                                }

                                LOGGER.info(
                                        "Accepted player: {}",
                                        EntityUtil.getName(player)
                                );

                                server.getPlayerManager()
                                      .onPlayerConnect(
                                              connection,
                                              player
                                      );

                                loginTimedOut.remove(EntityUtil.getName(player));
                            } else {
                                LOGGER.info(
                                        "Expired auth: {}",
                                        EntityUtil.getName(player)
                                );
                            }
                        },
                        // This handler will not be happened
                        e -> {
                            LOGGER.debug(
                                    "Exception in join server",
                                    e
                            );
                            if (server.isHost(player.getGameProfile())) {
                                LOGGER.debug(
                                        "Player {} lost status synchronize, but will not be process",
                                        PlayerUtil.getName(player)
                                );
                            } else {
                                LOGGER.debug("Player {} lost status synchronize",
                                             PlayerUtil.getName(player));

                                disconnect(TextUtil.literal("Lost status synchronize, please connect again")
                                                   .text());
                            }
                        }
                );
            }
        }
    }

    @Override
    public void onDisconnected(Text reason) {
        LOGGER.info(
                "{} lost connection: {}",
                this.player.getName()
                           .getString(),
                reason.getString()
        );
    }

    @Override
    public ClientConnection getConnection() {
        return this.connection;
    }
}
