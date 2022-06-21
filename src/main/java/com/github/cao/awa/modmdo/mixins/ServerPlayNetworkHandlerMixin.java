package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.event.client.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.event.server.chat.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

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

    @Shadow private boolean waitingForKeepAlive;

    @Shadow private long lastKeepAliveTime;

    @Shadow private long keepAliveId;

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


                    if (TOKEN_CHANNEL.equals(channel)) {
                        TRACKER.debug("Client are sent obsoleted login data");
                        serverLogin.reject(data1, oldLogin, "", TextUtil.literal("obsolete login type").text());
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

                            if (modMdoType == ModMdoType.SERVER) {
                                serverLogin.login(data1, data2, data3, data4, data5);
                            }
                        }
                    }

                    if (channel.equals(SUFFIX_CHANNEL)) {
                        LOGGER.debug("Client are sent suffix data: " + data1);
                        TRACKER.submit("Suffix data1: " + data1);
                        if (loginUsers.hasUser(player)) {
                            serverLogin.suffix(loginUsers.getUser(player), data1);
                        }
                    }
                });

                ci.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            serverLogin.logout(player);
            EntrustExecution.tryFor(modmdoConnections, processor -> processor.sendPlayerQuit(EntityUtil.getName(player)));
            event.submit(new QuitServerEvent(player, connection, player.getPos(), server));
        }
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"), cancellable = true)
    public void onDisconnected0(Text reason, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            if (! loginUsers.hasUser(player) && !(player.networkHandler.connection.getAddress() == null)) {
                ci.cancel();
            }
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