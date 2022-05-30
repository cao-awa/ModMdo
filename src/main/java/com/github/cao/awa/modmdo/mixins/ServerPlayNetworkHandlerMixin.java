package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.*;
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
                    if (TOKEN.equals(channel)) {
                        oldLogin = EntrustParser.tryCreate(buf::readString, "");
                    } else {
                        informationSign = new Identifier(EntrustParser.tryCreate(buf::readString, ""));
                    }
                    String data1 = EntrustParser.tryCreate(buf::readString, "");
                    String data2 = EntrustParser.tryCreate(buf::readString, "");
                    String data3 = EntrustParser.tryCreate(buf::readString, "");
                    String data4 = EntrustParser.tryCreate(buf::readString, "");
                    String data5 = EntrustParser.tryCreate(buf::readString, "");

                    if (TOKEN.equals(channel)) {
                        serverLogin.reject(data1, oldLogin, "", new LiteralText("obsolete login type"));
                        return;
                    }

                    if (channel.equals(CLIENT)) {
                        if (informationSign.equals(LOGIN)) {
                            if (modMdoType == ModMdoType.SERVER) {
                                serverLogin.login(data1, data2, data3, data4, data5);
                            }
                        }
                    }
                });

                ci.cancel();
            } catch (Exception e) {

            }
        }
    }

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            serverLogin.logout(player);
            EntrustExecution.tryFor(modmdoConnections, processor -> processor.sendPlayerQuit(player.getName().asString()));
            event.submitQuitServer(player, connection, player.getPos(), server);
        }
    }

    @Inject(method = "executeCommand", at = @At("HEAD"))
    private void executeCommand(String input, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            LOGGER.info(player.getName().asString() + "(" + player.getUuid().toString() + ") run the command: " + input);
        }
    }

    @Inject(method = "onClientSettings", at = @At("HEAD"))
    private void onClientSettings(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            event.submitClientSetting(player, packet, server);
        }
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    public void onGameMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            event.submitGameChat(player, packet, server);
            if (! packet.getChatMessage().startsWith("/")) {
                EntrustExecution.tryFor(modmdoConnections, processor -> processor.sendChat(packet.getChatMessage(), player.getName().asString()));
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;disconnect(Lnet/minecraft/text/Text;)V"))
    public void disconnect(ServerPlayNetworkHandler instance, Text reason) {
        if (SharedVariables.isActive()) {
            if (reason.getString().equals("disconnect.timeout")) {
                if (loginUsers.getUser(player).getLoginTime() > 10000) {
                    instance.disconnect(reason);
                } else {
                    waitingForKeepAlive = false;
                }
            }
        }
    }
}