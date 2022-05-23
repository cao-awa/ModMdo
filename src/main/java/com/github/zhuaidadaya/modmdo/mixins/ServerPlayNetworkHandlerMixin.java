package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.modmdo.lang.*;
import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

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
        if (extras != null && extras.isActive(EXTRA_ID)) {
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

                    if (TOKEN.equals(channel)) {
                        serverLogin.reject(data1, oldLogin, "", new LiteralText("obsolete login type"));
                        return;
                    }

                    if (channel.equals(CLIENT)) {
                        if (informationSign.equals(LOGIN)) {
                            if (modMdoType == ModMdoType.SERVER) {
                                serverLogin.login(data1, data2, data3, data4);
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
        if (extras != null && extras.isActive(EXTRA_ID)) {
            serverLogin.logout(player);
            EntrustExecution.tryFor(modmdoConnections, processor -> processor.sendPlayerQuit(player.getName().asString()));
        }
    }

    @Inject(method = "executeCommand", at = @At("HEAD"))
    private void executeCommand(String input, CallbackInfo ci) {
        if (extras != null && extras.isActive(EXTRA_ID)) {
            LOGGER.info(player.getName().asString() + "(" + player.getUuid().toString() + ") run the command: " + input);
        }
    }

    @Inject(method = "onClientSettings", at = @At("HEAD"))
    private void onClientSettings(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        if (extras != null && extras.isActive(EXTRA_ID)) {
            loginUsers.getUser(player).setLanguage(Language.of(packet.getLanguage()));
        }
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    public void onGameMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (extras != null && extras.isActive(EXTRA_ID)) {
            if (! packet.getChatMessage().startsWith("/")) {
                EntrustExecution.tryFor(modmdoConnections, processor -> processor.sendChat(packet.getChatMessage(), player.getName().asString()));
            }
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;disconnect(Lnet/minecraft/text/Text;)V"))
    public void disconnect(ServerPlayNetworkHandler instance, Text reason) {
        if (extras != null && extras.isActive(EXTRA_ID)) {
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