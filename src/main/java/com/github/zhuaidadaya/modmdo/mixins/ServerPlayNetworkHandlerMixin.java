package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.type.*;
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

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

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

    @Inject(method = "executeCommand", at = @At("HEAD"))
    private void executeCommand(String input, CallbackInfo ci) {
        LOGGER.info(player.getName().asString() + "(" + player.getUuid().toString() + ") run the command: " + input);
    }

    @Inject(method = "onClientSettings", at = @At("HEAD"))
    private void onClientSettings(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        loginUsers.getUser(player).setLanguage(Language.getLanguageForName(packet.language()));
    }
}