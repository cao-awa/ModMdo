package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.modMdo.commands.init.ArgumentInit;
import com.github.zhuaidadaya.modMdo.login.token.TokenContentType;
import com.github.zhuaidadaya.modMdo.jump.server.ServerUtil;
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

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

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
     *
     * @author 草二号机
     * @author 草
     * @author zhuaidadaya
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onOnCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        PacketByteBuf data = packet.getData();

        try {
            int id;
            id = data.readVarInt();

            LOGGER.info("server sent a payload id: " + id);

            switch(id) {
                case 99 -> {
                    String address = formatAddress(connection.getAddress());
                    String loginType = getModMdoTokenFormat(address, TokenContentType.LOGIN_TYPE);
                    String token;
                    if(jumpToken.equals("") & jumpLoginType.equals("")) {
                        token = getModMdoTokenFormat(address, TokenContentType.TOKEN_BY_ENCRYPTION);
                    } else {
                        token = jumpToken;
                        loginType = jumpLoginType;
                        jumpLoginType = "";
                        jumpToken = "";
                    }
                    UUID uuid = PlayerEntity.getUuidFromProfile(profile);
                    connection.send(new CustomPayloadC2SPacket(loginChannel, (new PacketByteBuf(Unpooled.buffer())).writeString(uuid.toString()).writeString(profile.getName()).writeString(loginType).writeString(token).writeString(address).writeString(String.valueOf(MODMDO_VERSION))));
                }
                case 96 -> {
                    String address = formatAddress(connection.getAddress());
                    String loginType = getModMdoTokenFormat(address, TokenContentType.LOGIN_TYPE);
                    UUID uuid = PlayerEntity.getUuidFromProfile(profile);
                    connection.send(new CustomPayloadC2SPacket(loginChannel, (new PacketByteBuf(Unpooled.buffer())).writeString(uuid.toString()).writeString(profile.getName()).writeString(loginType).writeString("Nan").writeString(address).writeString(String.valueOf(MODMDO_VERSION))));
                }
                case 105 -> {
                    String jumpName = "";
                    try {
                        jumpName = data.readString();
                    } catch (Exception ex) {

                    }

                    String token = "";
                    try {
                        token = data.readString();
                    } catch (Exception ex) {

                    }

                    String loginType = "";
                    try {
                        loginType = data.readString();
                    } catch (Exception ex) {

                    }

                    jumpToken = token;
                    jumpLoginType= loginType;
                    jump = jumpName;
                    connectTo = true;
                }
                case 106 -> {
                    String jumpName = "";
                    try {
                        jumpName = data.readString();
                    } catch (Exception ex) {

                    }

                    jumpToken = "";
                    jump = jumpName;
                    connectTo = true;
                }
                case 107 -> {
                    String serversInfo = "";
                    try {
                        serversInfo = data.readString();
                    } catch (Exception ex) {

                    }

                    servers = new ServerUtil(new JSONObject(serversInfo));

                    ArgumentInit.initServerJump();
                }
            }
        } catch (Exception e) {
            LOGGER.error("error in connecting ModMdo server", e);
        }
        ci.cancel();
    }

    @Inject(method = "onHealthUpdate", at = @At("HEAD"), cancellable = true)
    public void onHealthUpdate(HealthUpdateS2CPacket packet, CallbackInfo ci) {
        if(client.player == null) {
            ci.cancel();
        }
    }

    @Inject(method = "onExperienceBarUpdate", at = @At("HEAD"), cancellable = true)
    public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket packet, CallbackInfo ci) {
        if(client.player == null) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerAbilities", at = @At("HEAD"), cancellable = true)
    public void onPlayerAbilities(PlayerAbilitiesS2CPacket packet, CallbackInfo ci) {
        if(client.player == null) {
            ci.cancel();
        }
    }
}