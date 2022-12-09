package com.github.cao.awa.modmdo.mixins.client.play;

import com.github.cao.awa.modmdo.security.level.*;
import com.github.cao.awa.modmdo.utils.entity.player.*;
import com.github.cao.awa.modmdo.utils.file.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.encryption.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.authlib.*;
import io.netty.buffer.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.*;
import org.json.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.nio.charset.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {
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
     * @author 草awa
     * @author zhuaidadaya
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"))
    private void onOnCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        PacketByteBuf data = packet.getData();

        EntrustExecution.tryTemporary(
                () -> {
                    Identifier informationSign = EntrustParser.tryCreate(
                            data::readIdentifier,
                            new Identifier("")
                    );

                    if (informationSign.equals(CHECKING_CHANNEL) || informationSign.equals(LOGIN_CHANNEL)) {
                        SECURE_KEYS.load(staticConfig.getConfigJSONObject("private_key"));
                        Receptacle<String> serverId = Receptacle.of();
                        if (informationSign.equals(CHECKING_CHANNEL)) {
                            EntrustExecution.tryTemporary(() -> serverId.set(data.readString()));
                        }
                        TRACKER.submit(
                                "Server are requesting login data, as: " + informationSign,
                                () -> {
                                    EntrustExecution.notNull(
                                            staticConfig.get("secure_level"),
                                            level -> {
                                                SECURE_KEYS.setLevel(SecureLevel.of(level));
                                                TRACKER.submit("Changed config secure_level as " + level);
                                            }
                                    );
                                    String address = EntrustParser.tryCreate(
                                            () -> {
                                                String addr = connection.getAddress()
                                                                        .toString();
                                                return addr.substring(addr.indexOf("/") + 1);
                                            },
                                            connection.getAddress()
                                                      .toString()
                                    );
                                    boolean hasServerId = serverId.get() != null;
                                    String identifier = hasServerId ? SECURE_KEYS.use(
                                            serverId.get(),
                                            address
                                    ) : SECURE_KEYS.use(
                                            address,
                                            address
                                    );
                                    if (serverId.get() != null && identifier != null) {
                                        if (SECURE_KEYS.has(address) && ! SECURE_KEYS.has(serverId.get())) {
                                            EntrustExecution.notNull(
                                                    SECURE_KEYS.get(address),
                                                    k -> k.setServerId(serverId.get())
                                            );
                                            SECURE_KEYS.set(
                                                    SECURE_KEYS.get(address)
                                                               .getServerId(),
                                                    SECURE_KEYS.get(address)
                                            );
                                            TRACKER.submit("Changed server address " + address + " to id: " + serverId.get());
                                        }
                                        SECURE_KEYS.removeAddress(address);
                                        SECURE_KEYS.save();
                                    }
                                    String verifyKey = hasServerId ?
                                                       SECURE_KEYS.get(serverId.get())
                                                                  .getVerifyKey() :
                                                       null;
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
                                            "version",
                                            String.valueOf(MODMDO_VERSION)
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
                                        connection.send(new CustomPayloadC2SPacket(
                                                CLIENT_CHANNEL,
                                                (new PacketByteBuf(Unpooled.buffer())).writeString(LOGIN_CHANNEL.toString())
                                                                                      .writeString(loginData.toString())
                                        ));
                                    } else {
                                        String verifying;
                                        JSONObject verifyData = new JSONObject();
                                        if (identifier == null) {
                                            return;
                                        }
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
                                        verifying = EntrustParser.trying(
                                                () -> AES.aesEncryptToString(
                                                        verifyData.toString()
                                                                  .getBytes(StandardCharsets.ISO_8859_1),
                                                        verifyKey.getBytes()
                                                ),

                                                ex -> ""
                                        );
                                        loginData.put(
                                                "verifyData",
                                                verifying
                                        );
                                        loginData.put(
                                                "verifyKey",
                                                verifyKey
                                        );
                                        connection.send(new CustomPayloadC2SPacket(
                                                CLIENT_CHANNEL,
                                                (new PacketByteBuf(Unpooled.buffer())).writeString(LOGIN_CHANNEL.toString())
                                                                                      .writeString(loginData.toString())
                                        ));
                                    }
                                }
                        );
                    }
                },
                ex -> TRACKER.err(
                        "Error in connecting ModMdo server",
                        ex
                )
        );
    }
}
