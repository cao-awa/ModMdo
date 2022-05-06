package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.modmdo.lang.*;
import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

/**
 * TAG:DRT|SKP|VSD
 * 这个tag用于注明这是有版本差异的
 * 存在这个tag时不会直接从其他正在开发的部分复制
 * 而是手动替换
 * TAG:
 * DRT(Don't Replace It)
 * SKP(Skip)
 * VSD(Version Difference)
 * <p>
 * 手动替换检测: 1.17.x
 */
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
            Identifier channel = EntrustParser.tryCreate(packet::getChannel, new Identifier(""));

            PacketByteBuf packetByteBuf = EntrustParser.trying(() -> new PacketByteBuf(packet.getData().copy()));

            String oldLogin = "";
            Identifier informationSign = new Identifier("");
            if (TOKEN.equals(channel)) {
                oldLogin = EntrustParser.tryCreate(packetByteBuf::readString, "");
            } else {
                informationSign = new Identifier(EntrustParser.tryCreate(packetByteBuf::readString, ""));
            }
            String data1 = EntrustParser.tryCreate(packetByteBuf::readString, "");
            String data2 = EntrustParser.tryCreate(packetByteBuf::readString, "");
            String data3 = EntrustParser.tryCreate(packetByteBuf::readString, "");
            String data4 = EntrustParser.tryCreate(packetByteBuf::readString, "");

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

            ci.cancel();
        } catch (Exception e) {

        }
    }

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        serverLogin.logout(player);
    }

    @Shadow
    protected abstract boolean isHost();

    @Inject(method = "executeCommand", at = @At("HEAD"))
    private void executeCommand(String input, CallbackInfo ci) {
        LOGGER.info(player.getName().asString() + "(" + player.getUuid().toString() + ") run the command: " + input);
    }

    @Inject(method = "onClientSettings", at = @At("HEAD"))
    private void onClientSettings(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        loginUsers.getUser(player).setLanguage(Language.getLanguageForName(packet.getLanguage()));
        System.out.println(packet.getLanguage());
        System.out.println(loginUsers.getUser(player).getLanguage());
    }
}