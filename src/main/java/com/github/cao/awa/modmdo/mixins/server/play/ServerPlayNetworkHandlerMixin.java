package com.github.cao.awa.modmdo.mixins.server.play;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.client.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.event.server.chat.*;
import com.github.cao.awa.modmdo.lang.Dictionary;
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
import org.json.*;
import org.slf4j.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("ModMdoServerAuthHandler");

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
     * @author cao_awa
     * @author 草二号机
     * @author zhuaidadaya
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
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
                                            .text()
                            );
                            return;
                        }

                        if (CLIENT_CHANNEL.equals(channel)) {
                            LOGGER.debug("Processing client login data");
                            if (informationSign.equals(LOGIN_CHANNEL)) {
                                LOGGER.debug("Name: {}" ,name);
                                LOGGER.debug("UUID: {}" ,uuid);
                                LOGGER.debug("Identifier: {}" ,identifier);
                                LOGGER.debug("ModMdo Name: {}" ,modmdoName);
                                LOGGER.debug("Verify Data: {}" ,unidirectionalVerify);
                                LOGGER.debug("Verify Key: {}" ,verifyKey);

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

            ci.cancel();
        } catch (Exception e) {

        }
    }

    public boolean beforeLogin() {
        if (serverUnderDdosAttack.get()) {
            return false;
        }
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
                    LOGGER.warn("ModMdo rejected player '{}' login, because player are not whitelisted", name);
                } else {
                    LOGGER.warn("ModMdo rejected player '{}' login",name);
                }
                disc(rejected.getMessage() == null ?
                     TextUtil.translatable("multiplayer.disconnect.not_whitelisted")
                             .text() :
                     rejected.getMessage());

                rejectUsers.removeUser(player);

                LOGGER.info("Rejected player: " + name);
                return;
            } else {
                if (loginTimedOut.containsKey(name)) {
                    if (loginTimedOut.get(name) < TimeUtil.millions()) {
                        disc(TextUtil.literal("Login timed out")
                                     .text());
                        LOGGER.warn("ModMdo rejected player '{}' login, because player not sent login request", name);

                        LOGGER.info("Rejected player: {}", name);
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
                    LOGGER.info("Player {} has been banned form server",PlayerUtil.getName(player));
                } else {
                    disc(minecraftTextFormat.format(
                                                    new Dictionary(certificate.getLastLanguage()),
                                                    "multiplayer.disconnect.banned-indefinite"
                                            )
                                            .text());
                    LOGGER.info("Player {} has been banned form server", PlayerUtil.getName(player));
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
                                        disc(Translatable.translatable("multiplayer.disconnect.not_whitelisted")
                                                         .text());
                                    }
                                }

                                LOGGER.info("Accepted player: {}", EntityUtil.getName(player));

                                server.getPlayerManager()
                                      .onPlayerConnect(
                                              connection,
                                              player
                                      );

                                loginTimedOut.remove(EntityUtil.getName(player));
                            } else {
                                LOGGER.info("Expired auth: {}", EntityUtil.getName(player));
                            }
                        },
                        // This handler will not be happened
                        e -> {
                            LOGGER.debug(
                                    "Exception in join server",
                                    e
                            );
                            if (server.isHost(player.getGameProfile())) {
                                LOGGER.debug("Player {} lost status synchronize, but will not be process", PlayerUtil.getName(player));
                            } else {
                                LOGGER.debug("Player {} lost status synchronize",PlayerUtil.getName(player));

                                disc(TextUtil.literal("Lost status synchronize, please connect again")
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

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void onDisconnected0(PlayerManager instance, Text message, MessageType type, UUID sender) {
        if (loginUsers.hasUser(player) || player.networkHandler.connection.getAddress() == null) {
            instance.broadcast(
                    message,
                    type,
                    sender
            );
        }
    }

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    public void onDisconnected0(Logger instance, String s, Object o1, Object o2) {
        if (serverUnderDdosAttack.get()) {
            return;
        }
        instance.info(s, o1, o2);
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

    @Inject(method = "onChatMessage", at = @At("HEAD"))
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
