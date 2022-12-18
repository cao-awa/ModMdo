package com.github.cao.awa.modmdo.mixins.client.play;

import com.github.cao.awa.modmdo.security.level.*;
import com.github.cao.awa.modmdo.utils.digger.*;
import com.github.cao.awa.modmdo.utils.encryption.*;
import com.github.cao.awa.modmdo.utils.entity.player.*;
import com.github.cao.awa.modmdo.utils.packet.buf.*;
import com.github.cao.awa.modmdo.utils.packet.sender.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.authlib.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;
import org.json.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.nio.charset.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;
import static com.github.cao.awa.modmdo.storage.SharedVariables.INFO_CHANNEL;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoClientAuthHandler");

    @Shadow
    @Final
    private ClientConnection connection;

    @Shadow
    @Final
    private GameProfile profile;

    @Shadow
    @Final
    private MinecraftClient client;

    /**
     * 与服务端进行自定义通信
     *
     * @param packet
     *         packet
     * @param ci
     *         callback
     * @author 草二号机
     * @author cao_awa
     * @author zhuaidadaya
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onOnCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        Receptacle<Boolean> doCancel = Receptacle.of(false);

        EntrustEnvironment.trys(
                () -> {
                    final PacketDataProcessor processor = new PacketDataProcessor(packet.getData());

                    final Identifier informationSign = processor.readIdentifier();

                    final ClientPacketSender sender = new ClientPacketSender(
                            connection,
                            CLIENT_CHANNEL
                    );

                    if (informationSign.equals(CHECKING_CHANNEL)) {
                        SECURE_KEYS.load(staticConfig.getJSONObject("private_key"));
                        final String serverId = EntrustEnvironment.receptacle(receptacle -> {
                            receptacle.set(processor.readString());
                        });
                        LOGGER.debug(
                                "Server are requesting login data, as: {}",
                                informationSign
                        );
                        EntrustEnvironment.notNull(
                                staticConfig.getString("secure_level"),
                                level -> {
                                    SECURE_KEYS.setLevel(SecureLevel.of(level));
                                    LOGGER.debug("Changed config secure_level as " + level);
                                }
                        );
                        final String address = EntrustEnvironment.get(
                                () -> {
                                    String addr = connection.getAddress()
                                                            .toString();
                                    return addr.substring(addr.indexOf("/") + 1);
                                },
                                connection.getAddress()
                                          .toString()
                        );
                        final String identifier = serverId != null ? SECURE_KEYS.use(
                                serverId,
                                address
                        ) : SECURE_KEYS.use(
                                address,
                                address
                        );
                        if (serverId != null && identifier != null) {
                            if (SECURE_KEYS.has(address) && ! SECURE_KEYS.has(serverId)) {
                                EntrustEnvironment.notNull(
                                        SECURE_KEYS.get(address),
                                        k -> k.setServerId(serverId)
                                );
                                SECURE_KEYS.set(
                                        SECURE_KEYS.get(address)
                                                   .getServerId(),
                                        SECURE_KEYS.get(address)
                                );
                                LOGGER.debug(
                                        "Changed server address '{}' to id: {}",
                                        address,
                                        serverId
                                );
                            }
                            SECURE_KEYS.removeAddress(address);
                            SECURE_KEYS.save();
                        }
                        String verifyKey = serverId == null ?
                                           null :
                                           SECURE_KEYS.get(serverId)
                                                      .getVerifyKey();
                        JSONObject loginData = new JSONObject();
                        loginData.put(
                                "name",
                                profile.getName()
                        );
                        loginData.put(
                                "uuid",
                                PlayerUtil.getUUID(profile)
                                          .toString()
                        );
                        loginData.put(
                                "identifier",
                                identifier
                        );
                        loginData.put(
                                "versionName",
                                MODMDO_VERSION_NAME
                        );

                        if (verifyKey == null) {
                            loginData.put(
                                    "verifyData",
                                    ""
                            );
                            loginData.put(
                                    "verifyKey",
                                    ""
                            );
                        } else {
                            if (identifier == null) {
                                return;
                            }
                            JSONObject verifyData = new JSONObject();
                            verifyData.put(
                                    "identifier",
                                    EntrustEnvironment.get(
                                            () -> MessageDigger.digest(
                                                    identifier,
                                                    MessageDigger.Sha3.SHA_512
                                            ),
                                            identifier
                                    )
                            );
                            loginData.put(
                                    "verifyData",
                                    EntrustEnvironment.trys(
                                            () -> AES.encryptToString(
                                                    verifyData.toString()
                                                              .getBytes(StandardCharsets.ISO_8859_1),
                                                    verifyKey.getBytes()
                                            ),
                                            ex -> ""
                                    )
                            );
                            loginData.put(
                                    "verifyKey",
                                    verifyKey
                            );
                        }

                        sender.custom()
                              .write(LOGIN_CHANNEL)
                              .write(loginData)
                              .send();
                    } else if (informationSign.equals(LOGIN_CHANNEL)) {
                        JSONObject loginData = new JSONObject();
                        loginData.put(
                                "versionName",
                                MODMDO_VERSION_NAME
                        );
                        sender.custom()
                              .write(INFO_CHANNEL)
                              .write(loginData)
                              .send();
                    }

                    doCancel.set(true);
                },
                ex -> LOGGER.error(
                        "Error in connecting ModMdo server",
                        ex
                )
        );

        if (doCancel.get()) {
            ci.cancel();
        }
    }
}
