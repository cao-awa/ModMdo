package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.authlib.*;
import io.netty.buffer.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.world.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import org.json.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {
    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    @Final
    private ClientConnection connection;

    @Shadow
    private Set<RegistryKey<World>> worldKeys;

    @Shadow
    private DynamicRegistryManager registryManager;

    @Shadow
    private int chunkLoadDistance;

    @Shadow
    private ClientWorld.Properties worldProperties;

    @Shadow
    private ClientWorld world;

    @Shadow
    @Final
    private GameProfile profile;

    /**
     * 如果收到了服务器的包,确定对方是一个ModMdo服务器并开启Token加密才发送数据包
     * 如果服务器未安装ModMdo或未开启加密, 则不发送
     *
     * @param packet
     *         packet
     * @param ci
     *         callback
     * @author 草二号机
     * @author 草
     * @author zhuaidadaya
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onOnCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        PacketByteBuf data = packet.getData();

        try {
            Identifier channel = packet.getChannel();

            Identifier informationSign = data.readIdentifier();

            LOGGER.info("server sent a payload in channel: " + channel);

            if (informationSign.equals(CHECKING) || informationSign.equals(LOGIN)) {
                connection.send(new CustomPayloadC2SPacket(CLIENT, (new PacketByteBuf(Unpooled.buffer())).writeIdentifier(LOGIN).writeString(profile.getName()).writeString(PlayerEntity.getUuidFromProfile(profile).toString()).writeString(EntrustParser.getNotNull(configCached.getConfigString("identifier"), "")).writeString(String.valueOf(MODMDO_VERSION))));
            }

            if (informationSign.equals(DATA)) {
                String data1 = EntrustParser.tryCreate(data::readString, "");
                String data2 = EntrustParser.tryCreate(data::readString, "");

                switch (data1) {
                    case "whitelist_names" -> {
                        JSONObject json = new JSONObject(data2);
                        for (Object o : json.getJSONArray("names")) {
                            whitelist.put(o.toString(), null);
                        }
                    }
                    case "temporary_whitelist_names" -> {
                        JSONObject json = new JSONObject(data2);
                        for (Object o : json.getJSONArray("names")) {
                            temporaryWhitelist.put(o.toString(), null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("error in connecting ModMdo server", e);
        }
        ci.cancel();
    }
}