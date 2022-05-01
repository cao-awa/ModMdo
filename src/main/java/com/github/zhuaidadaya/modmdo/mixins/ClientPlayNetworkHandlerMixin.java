package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.modmdo.identifier.*;
import com.github.zhuaidadaya.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.util.*;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

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
            Identifier channel = new Identifier(data.readString());

            LOGGER.info("server sent a payload: " + channel);

            if (channel.equals(CHECKING) || channel.equals(LOGIN)) {
                connection.send(new CustomPayloadC2SPacket(LOGIN, (new PacketByteBuf(Unpooled.buffer())).writeString(profile.getName()).writeString(PlayerEntity.getUuidFromProfile(profile).toString()).writeString(EntrustParser.getNotNull(configCached.getConfigString("identifier"), "")).writeString(String.valueOf(MODMDO_VERSION))));
            }
        } catch (Exception e) {
            LOGGER.error("error in connecting ModMdo server", e);
        }
        ci.cancel();
    }
}