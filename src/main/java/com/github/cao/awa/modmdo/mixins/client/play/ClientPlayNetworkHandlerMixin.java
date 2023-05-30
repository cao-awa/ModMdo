package com.github.cao.awa.modmdo.mixins.client.play;

import com.github.cao.awa.modmdo.security.key.*;
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
import com.alibaba.fastjson2.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.nio.charset.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

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
     * Exchanges the login information to server.
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
        // Cancel next steps action if are packet is for modmdo information
        Receptacle<Boolean> doCancel = Receptacle.of(false);

        EntrustEnvironment.trys(
                () -> {
                    // Let data processor help to handle reading packet
                    final PacketDataProcessor processor = new PacketDataProcessor(packet.getData());

                    final Identifier informationSign = processor.readIdentifier();

                    // Let packet sender help to handle sending packet
                    final ClientPacketSender sender = new ClientPacketSender(
                            connection,
                            CLIENT_CHANNEL
                    );

                    // Prepare login if sign is 'Checking channel'
                    if (informationSign.equals(CHECKING_CHANNEL)) {
                        SECURE_KEYS.load(staticConfig.getJSONObject("private_key"));
                        // Read server identifier
                        final String serverId = EntrustEnvironment.receptacle(receptacle -> receptacle.set(processor.readString()));
                        LOGGER.debug(
                                "Server are requesting login data, as: '{}'",
                                informationSign
                        );

                        // Read client secure level
                        EntrustEnvironment.notNull(
                                staticConfig.getString("secure_level"),
                                level -> {
                                    SECURE_KEYS.setLevel(SecureLevel.of(level));
                                    LOGGER.debug("Changed config secure_level as " + level);
                                }
                        );

                        // Parse server address
                        final String address = EntrustEnvironment.get(
                                () -> {
                                    String addr = connection.getAddress()
                                                            .toString();
                                    return addr.substring(addr.indexOf("/") + 1);
                                },
                                connection.getAddress()
                                          .toString()
                        );

                        // Calculate client identifier
                        // Use server identifier to find special identifier
                        // Use address to find it if server did not have identifier
                        final String identifier = serverId != null ? SECURE_KEYS.use(
                                serverId,
                                address
                        ) : SECURE_KEYS.use(
                                address,
                                address
                        );
                        // Usually, happens no server identifier is first login to server
                        // So modmdo need to update the server identifier if server has the identifier
                        // And client identifier also must not null
                        if (serverId != null && identifier != null) {
                            // Should have address can be found previously
                            // And cannot find to identifier
                            if (SECURE_KEYS.has(address) && ! SECURE_KEYS.has(serverId)) {
                                // Convert it to server identifier format
                                SecureKey key = SECURE_KEYS.get(address);
                                // I do not know why it can be null, just do not remove it
                                if (key != null) {
                                    key.setServerId(serverId);
                                }
                                // Complete convert
                                SECURE_KEYS.set(
                                        serverId,
                                        key
                                );
                                LOGGER.debug(
                                        "Changed server address '{}' to id: {}",
                                        address,
                                        serverId
                                );
                            }
                            // Finishing work, let address format expired be removed
                            SECURE_KEYS.removeAddress(address);

                            // Save changes, will replace secure keys to service mode in the future, this step can be removed
                            SECURE_KEYS.save();
                        }
                        // Loading special verify key for server
                        // No verify key if server are did not have identifier
                        String verifyKey = serverId == null ?
                                           null :
                                           SECURE_KEYS.get(serverId)
                                                      .getVerifyKey();
                        // Create default login data
                        JSONObject loginData = new JSONObject();
                        loginData.fluentPut(
                                         "name",
                                         profile.getName()
                                 )
                                 .fluentPut(
                                         "uuid",
                                         PlayerUtil.getUUID(profile)
                                                   .toString()
                                 )
                                 .fluentPut(
                                         "identifier",
                                         identifier
                                 )
                                 .fluentPut(
                                         "versionName",
                                         MODMDO_VERSION_NAME
                                 )
                                 .fluentPut(
                                         "verifyData",
                                         ""
                                 )
                                 .fluentPut(
                                         "verifyKey",
                                         ""
                                 );

                        // Calculate encrypt data if server verify key can be load
                        if (verifyKey != null) {
                            // Cannot lose identifier when verify key is valid
                            if (identifier == null) {
                                return;
                            }
                            // Replaces verify data
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
                            loginData.fluentPut(
                                             "verifyData",
                                             EntrustEnvironment.trys(
                                                     () -> AES.encryptToString(
                                                             verifyData.toString()
                                                                       .getBytes(StandardCharsets.ISO_8859_1),
                                                             verifyKey.getBytes()
                                                     ),
                                                     ex -> ""
                                             )
                                     )
                                     .fluentPut(
                                             "verifyKey",
                                             verifyKey
                                     );
                        }

                        // Login to server
                        sender.custom()
                              .write(LOGIN_CHANNEL)
                              .write(loginData)
                              .send();
                    } else if (informationSign.equals(LOGIN_CHANNEL)) {
                        // Prepare version name update if sign is 'Login channel'
                        JSONObject versionData = new JSONObject();
                        versionData.put(
                                "versionName",
                                MODMDO_VERSION_NAME
                        );

                        // Update version name
                        sender.custom()
                              .write(INFO_CHANNEL)
                              .write(versionData)
                              .send();
                    }

                    // Is handled, cancel vanilla actions
                    doCancel.set(true);
                },
                // Failed handle login request
                ex -> LOGGER.error(
                        "Error in connecting ModMdo server",
                        ex
                )
        );

        // Cancel next steps action
        if (doCancel.get()) {
            ci.cancel();
        }
    }
}
