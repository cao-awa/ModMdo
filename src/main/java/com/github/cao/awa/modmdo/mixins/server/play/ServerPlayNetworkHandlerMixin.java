package com.github.cao.awa.modmdo.mixins.server.play;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.client.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.event.server.chat.*;
import com.github.cao.awa.modmdo.lang.Dictionary;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.entity.player.*;
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
import org.json.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    @Final
    public ClientConnection connection;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    private boolean waitingForKeepAlive;

    @Shadow
    private long lastKeepAliveTime;

    @Shadow
    private long keepAliveId;

    /**
     * 与客户端进行自定义通信
     * <br>
     *
     * @param packet
     *         客户端发送的数据包
     * @param ci
     *         callback
     * @author 草awa
     * @author 草二号机
     * @author zhuaidadaya
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        try {
            Identifier channel = EntrustEnvironment.get(
                    packet::getChannel,
                    new Identifier("")
            );

            PacketByteBuf packetByteBuf = EntrustEnvironment.trys(() -> new PacketByteBuf(packet.getData()
                                                                                                .copy()));

            EntrustExecution.notNull(
                    packetByteBuf,
                    buf -> {
                        String oldLogin = "";
                        Identifier informationSign = new Identifier("");
                        if (TOKEN_CHANNEL.equals(channel)) {
                            oldLogin = EntrustEnvironment.get(
                                    buf::readString,
                                    ""
                            );
                        } else {
                            informationSign = new Identifier(EntrustEnvironment.get(
                                    buf::readString,
                                    ""
                            ));
                        }
                        JSONObject loginData = new JSONObject(EntrustEnvironment.get(
                                buf::readString,
                                ""
                        ));
                        //
                        String name = loginData.getString("name");
                        String uuid = loginData.getString("uuid");
                        String identifier = loginData.getString("identifier");
                        String modmdoVersion = loginData.getString("version");
                        String modmdoName = loginData.getString("versionName");
                        String unidirectionalVerify = loginData.getString("verifyData");
                        String verifyKey = loginData.getString("verifyKey");

                        if (TOKEN_CHANNEL.equals(channel)) {
                            TRACKER.debug("Processing client obsoleted login data");
                            serverLogin.reject(
                                    name,
                                    oldLogin,
                                    "",
                                    TextUtil.literal("Obsolete login type")
                                            .text()
                            );
                            return;
                        }

                        if (CLIENT_CHANNEL.equals(channel)) {
                            TRACKER.debug("Processing client login data");
                            if (informationSign.equals(LOGIN_CHANNEL)) {
                                TRACKER.submit("Login data1: " + name);
                                TRACKER.submit("Login data2: " + uuid);
                                TRACKER.submit("Login data3: " + identifier);
                                TRACKER.submit("Login data4: " + modmdoVersion);
                                TRACKER.submit("Login data5: " + modmdoName);
                                TRACKER.submit("Login data6: " + unidirectionalVerify);
                                TRACKER.submit("Login data7: " + verifyKey);

                                if (modMdoType == ModMdoType.SERVER) {
                                    if (beforeLogin()) {
                                        serverLogin.login(
                                                name,
                                                uuid,
                                                identifier,
                                                modmdoVersion,
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

            ci.cancel();
        } catch (Exception e) {

        }
    }

    public boolean beforeLogin() {
        String name = EntityUtil.getName(player);
        if (server.getPlayerManager()
                  .getPlayer(name) != null) {
            return false;
        }
        if (loginUsers.hasUser(name)) {
            disc(Translatable.translatable("login.dump.rejected")
                             .text());
            return false;
        }
        return true;
    }

    public void disc(Text reason) {
        this.connection.send(new DisconnectS2CPacket(reason));
        this.connection.disconnect(reason);
    }

    public void afterLogin() {
        if (modmdoWhitelist) {
            String name = EntityUtil.getName(player);
            if (rejectUsers.hasUser(player)) {
                User rejected = rejectUsers.getUser(player.getUuid());
                if (rejected.getMessage() == null) {
                    TRACKER.warn("ModMdo rejected player '" + name + "' login, because player are not whitelisted");
                } else {
                    TRACKER.warn("ModMdo rejected player '" + name + "' login");
                }
                disc(rejected.getMessage() == null ?
                     TextUtil.translatable("multiplayer.disconnect.not_whitelisted")
                             .text() :
                     rejected.getMessage());

                rejectUsers.removeUser(player);

                TRACKER.info("Rejected player: " + name);
                return;
            } else {
                if (loginTimedOut.containsKey(name)) {
                    if (loginTimedOut.get(name) < TimeUtil.millions()) {
                        disc(TextUtil.literal("Login timed out")
                                     .text());
                        TRACKER.warn("ModMdo rejected player '" + name + "' login, because player not sent login request");

                        TRACKER.info("Rejected player: " + name);
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
                    disc(minecraftTextFormat.format(
                                                    new com.github.cao.awa.modmdo.lang.Dictionary(certificate.getLastLanguage()),
                                                    "multiplayer.disconnect.banned-time-limited",
                                                    remaining
                                            )
                                            .text());
                    TRACKER.info("Player " + PlayerUtil.getName(player) + " has been banned form server");
                } else {
                    disc(minecraftTextFormat.format(
                                                    new Dictionary(certificate.getLastLanguage()),
                                                    "multiplayer.disconnect.banned-indefinite"
                                            )
                                            .text());
                    TRACKER.info("Player " + PlayerUtil.getName(player) + " has been banned form server");
                }
            } else {
                EntrustEnvironment.trys(
                        () -> {
                            if (connection.isOpen()) {
                                if (! loginUsers.hasUser(player)) {
                                    if (! config.getConfigBoolean("modmdo_whitelist")) {
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
                                        disc(Translatable.translatable("multiplayer.disconnect.not_whitelisted")
                                                         .text());
                                    }
                                }

                                TRACKER.info("Accepted player: " + EntityUtil.getName(player));

                                server.getPlayerManager()
                                      .onPlayerConnect(
                                              connection,
                                              player
                                      );

                                loginTimedOut.remove(EntityUtil.getName(player));
                            } else {
                                TRACKER.info("Expired nano: " + EntityUtil.getName(player));
                            }
                        },
                        // This handler will not be happened
                        e -> {
                            TRACKER.submit(
                                    "Exception in join server",
                                    e
                            );
                            if (server.isHost(player.getGameProfile())) {
                                TRACKER.debug("player " + PlayerUtil.getName(player) + " lost status synchronize, but will not be process");
                            } else {
                                TRACKER.debug("player " + PlayerUtil.getName(player) + " lost status synchronize");

                                disc(TextUtil.literal("lost status synchronize, please connect again")
                                             .text());
                            }
                        }
                );
            }
        }
    }

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        serverLogin.logout(player);
        event.submit(new QuitServerEvent(
                player,
                connection,
                player.getPos(),
                server
        ));
    }

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void onDisconnected0(PlayerManager instance, Text message, MessageType type, UUID sender) {
        if (loginUsers.hasUser(player) || player.networkHandler.connection.getAddress() == null) {
            instance.broadcastChatMessage(
                    message,
                    type,
                    sender
            );
        }
    }

    @Inject(method = "executeCommand", at = @At("HEAD"))
    private void executeCommand(String input, CallbackInfo ci) {
        LOGGER.info("'" + EntityUtil.getName(player) + "' run command: " + input);
    }

    @Inject(method = "onClientSettings", at = @At("HEAD"))
    private void onClientSettings(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        event.submit(new ClientSettingEvent(
                player,
                packet,
                server
        ));
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    public void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        event.submit(new GameChatEvent(
                player,
                packet,
                server
        ));
    }

    @Inject(method = "onKeepAlive", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;disconnect(Lnet/minecraft/text/Text;)V"), cancellable = true)
    public void disconnect(KeepAliveC2SPacket packet, CallbackInfo ci) {
        if (loginUsers.hasUser(player)) {
            if (TimeUtil.processMillion(loginUsers.getUser(player)
                                                  .getLoginTime()) > 10000) {
                this.disc(TextUtil.translatable("disconnect.timeout")
                                  .text());
            } else {
                waitingForKeepAlive = false;
                lastKeepAliveTime = TimeUtil.millions();
                keepAliveId = lastKeepAliveTime;
            }

            ci.cancel();
        }
    }

    @Shadow
    protected abstract boolean isHost();
}
