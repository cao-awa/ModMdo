package com.github.cao.awa.modmdo.mixins.server.play;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.client.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.event.server.chat.*;
import com.github.cao.awa.modmdo.lang.Dictionary;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.entity.*;
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
        if (SharedVariables.isActive()) {
            try {
                Identifier channel = EntrustParser.tryCreate(packet::getChannel, new Identifier(""));

                PacketByteBuf packetByteBuf = EntrustParser.trying(() -> new PacketByteBuf(packet.getData().copy()));

                EntrustExecution.notNull(packetByteBuf, buf -> {
                    String oldLogin = "";
                    Identifier informationSign = new Identifier("");
                    if (TOKEN_CHANNEL.equals(channel)) {
                        oldLogin = EntrustParser.tryCreate(buf::readString, "");
                    } else {
                        informationSign = new Identifier(EntrustParser.tryCreate(buf::readString, ""));
                    }
                    String data1 = EntrustParser.tryCreate(buf::readString, "");
                    String data2 = EntrustParser.tryCreate(buf::readString, "");
                    String data3 = EntrustParser.tryCreate(buf::readString, "");
                    String data4 = EntrustParser.tryCreate(buf::readString, "");
                    String data5 = EntrustParser.tryCreate(buf::readString, "");
                    String data6 = EntrustParser.tryCreate(buf::readString, "");
                    String data7 = EntrustParser.tryCreate(buf::readString, "");

                    if (TOKEN_CHANNEL.equals(channel)) {
                        TRACKER.debug("Client are sent obsoleted login data");
                        serverLogin.reject(data1, oldLogin, "", TextUtil.literal("obsolete login type"));
                        return;
                    }

                    if (channel.equals(CLIENT_CHANNEL)) {
                        TRACKER.debug("Client are sent login data");
                        if (informationSign.equals(LOGIN_CHANNEL)) {
                            TRACKER.submit("Login data1: " + data1);
                            TRACKER.submit("Login data2: " + data2);
                            TRACKER.submit("Login data3: " + data3);
                            TRACKER.submit("Login data4: " + data4);
                            TRACKER.submit("Login data5: " + data5);
                            TRACKER.submit("Login data6: " + data6);
                            TRACKER.submit("Login data7: " + data7);

                            if (modMdoType == ModMdoType.SERVER) {
                                serverLogin.login(data1, data2, data3, data4, data5, data6, data7);
                                beforeLogin();
                            }
                        }
                    }
                });

                ci.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void beforeLogin() {
        if (config.getConfigBoolean("modmdo_whitelist") && !server.isHost(player.getGameProfile())) {
            String name = EntityUtil.getName(player);
            if (loginUsers.hasUser(name) || server.getPlayerManager().getPlayer(name) != null) {
                disc(Translatable.translatable("login.dump.rejected"));
                return;
            }
            if (modMdoType == ModMdoType.SERVER && modmdoWhitelist) {
                if (rejectUsers.hasUser(player)) {
                    User rejected = rejectUsers.getUser(player.getUuid());
                    if (rejected.getMessage() == null) {
                        TRACKER.warn("ModMdo reject a login request, player \"" + name + "\", because player are not white-listed");
                    } else {
                        TRACKER.warn("ModMdo reject a login request, player \"" + name + "\"");
                    }
                    disc(rejected.getMessage() == null ? TextUtil.translatable("multiplayer.disconnect.not_whitelisted") : rejected.getMessage());

                    rejectUsers.removeUser(player);

                    TRACKER.info("Rejected player: " + name);
                    return;
                } else {
                    if (loginTimedOut.containsKey(name)) {
                        if (loginTimedOut.get(name) < TimeUtil.millions()) {
                            disc(TextUtil.literal("You are failed login because too long did not received login request"));
                            TRACKER.warn("ModMdo reject a login request, player \"" + name + "\", because player not sent login request");

                            TRACKER.info("Rejected player: " + name);
                            return;
                        }
                    }
                }

                if (! connection.isOpen()) {
                    return;
                }
            }

            if (handleBanned(player)) {
                Certificate ban = banned.get(name);
                if (ban instanceof TemporaryCertificate temporary) {
                    String remaining = temporary.formatRemaining();
                    player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(new com.github.cao.awa.modmdo.lang.Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining)));
                    player.networkHandler.connection.disconnect(minecraftTextFormat.format(new com.github.cao.awa.modmdo.lang.Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining));
                    TRACKER.info("Player " + name + " has been banned form server");
                } else {
                    player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(new com.github.cao.awa.modmdo.lang.Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite")));
                    player.networkHandler.connection.disconnect(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite"));
                    TRACKER.info("Player " + name + " has been banned form server");
                }
            }

            EntrustExecution.tryTemporary(() -> {
                EntrustExecution.tryTemporary(() -> {
                    if (connection.isOpen()) {
                        if (! loginUsers.hasUser(player)) {
                            if (! config.getConfigBoolean("modmdo_whitelist")) {
                                serverLogin.login(player.getName().getString(), player.getUuid().toString(), "", "", null, null);
                            } else {
                                disc(Translatable.translatable("multiplayer.disconnect.not_whitelisted"));
                            }
                        }

                        TRACKER.debug("Call: onPlayerConnect:(PlayerManager/ServerPlayNetworkHandlerMixin/Lambda Tg1) Tg1");

                        server.getPlayerManager().onPlayerConnect(connection, player);

                        TRACKER.info("Accepted player: " + name);

                        loginTimedOut.remove(name);
                    } else {
                        TRACKER.info("Expired nano: " + name);
                    }
                }, e -> {
                    TRACKER.submit("Exception in join server", e);
                    if (! server.isHost(player.getGameProfile())) {
                        TRACKER.debug("player " + name + " lost status synchronize");

                        disc(TextUtil.literal("lost status synchronize, please connect again"));
                    } else {
                        TRACKER.debug("player " + name + " lost status synchronize, but will not be process");
                    }
                });
            }, e -> TRACKER.submit("Exception in handle status sync lost", e));
        }
    }

    public void disc(Text reason) {
        this.connection.send(new DisconnectS2CPacket(reason));
        this.connection.disconnect(reason);
    }

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            serverLogin.logout(player);
            EntrustExecution.tryFor(modmdoConnections, processor -> processor.sendPlayerQuit(EntityUtil.getName(player)));
            event.submit(new QuitServerEvent(player, connection, player.getPos(), server));
        }
    }

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void onDisconnected0(PlayerManager instance, Text message, MessageType type, UUID sender) {
        if (SharedVariables.isActive()) {
            if (loginUsers.hasUser(player) || player.networkHandler.connection.getAddress() == null) {
                instance.broadcastChatMessage(message, type, sender);
            }
        } else {
            instance.broadcastChatMessage(message, type, sender);
        }
    }

    @Inject(method = "executeCommand", at = @At("HEAD"))
    private void executeCommand(String input, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            LOGGER.info(EntityUtil.getName(player) + "(" + player.getUuid().toString() + ") run the command: " + input);
        }
    }

    @Inject(method = "onClientSettings", at = @At("HEAD"))
    private void onClientSettings(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            event.submit(new ClientSettingEvent(player, packet, server));
        }
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    public void onGameMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            event.submit(new GameChatEvent(player, packet, server));
            if (! packet.getChatMessage().startsWith("/")) {
                EntrustExecution.tryFor(modmdoConnections, processor -> processor.sendChat(packet.getChatMessage(), EntityUtil.getName(player)));
            }
        }
    }

    @Redirect(method = "onKeepAlive", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;disconnect(Lnet/minecraft/text/Text;)V"))
    public void disconnect(ServerPlayNetworkHandler instance, Text reason) {
        if (SharedVariables.isActive()) {
            if (reason.getString().equals("disconnect.timeout")) {
                if (TimeUtil.processMillion(loginUsers.getUser(player).getLoginTime()) > 10000) {
                    instance.disconnect(reason);
                } else {
                    waitingForKeepAlive = false;
                    lastKeepAliveTime = TimeUtil.millions();
                    keepAliveId = lastKeepAliveTime;
                }
            }
        }
    }
}
