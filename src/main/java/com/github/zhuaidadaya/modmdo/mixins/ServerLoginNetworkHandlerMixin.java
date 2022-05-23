package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import com.github.zhuaidadaya.modmdo.utils.times.TimeUtil;
import com.github.zhuaidadaya.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin implements ServerLoginPacketListener {
    @Shadow
    @Final
    public ClientConnection connection;
    boolean authing = false;
    boolean preReject = false;

    @Shadow
    @Final
    MinecraftServer server;

    @Shadow
    @Nullable GameProfile profile;

    /**
     * 如果玩家为null, 则拒绝将玩家添加进服务器
     * (因为其他地方有cancel, 所以可能null)
     *
     * @param player
     *         玩家
     * @author 草awa
     * @author 草二号机
     * @reason
     */
    @Overwrite
    private void addToServer(ServerPlayerEntity player) {
        if (extras != null && extras.isActive(EXTRA_ID)) {
            if (player == null)
                return;

            if (profile == null || profile.getId() == null && preReject) {
                return;
            }

            if (config.getConfigBoolean("compatible_online_mode")) {
                EntrustExecution.tryTemporary(() -> {
                    serverLogin.loginUsingYgg(player.getName().asString(), profile.getId().toString());
                }, () -> {
                    serverLogin.reject(player.getName().asString(), profile.getId().toString(), "", new TranslatableText("multiplayer.disconnect.not_whitelisted"));
                });
            }

            if (! server.isHost(player.getGameProfile()) || modMdoType == ModMdoType.SERVER) {
                new Thread(() -> {
                    Thread.currentThread().setName("ModMdo accepting");
                    long nano = System.nanoTime();
                    LOGGER.info("nano " + nano + " (" + player.getName().asString() + ") trying join server");

                    long waiting = TimeUtil.millions();

                    int loginCheckTimeLimit = config.getConfigInt("checker_time_limit");

                    try {
                        ServerPlayNetworkHandler handler = new ServerPlayNetworkHandler(server, connection, player);
                        handler.sendPacket(new CustomPayloadS2CPacket(SERVER, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA).writeString("modmdo-connection")));
                        handler.sendPacket(new CustomPayloadS2CPacket(SERVER, new PacketByteBuf(Unpooled.buffer()).writeVarInt(modmdoWhitelist ? 99 : 96)));
                        handler.sendPacket(new CustomPayloadS2CPacket(SERVER, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(modmdoWhitelist ? CHECKING : LOGIN)));
                    } catch (Exception e) {

                    }

                    if (modMdoType == ModMdoType.SERVER & modmdoWhitelist) {
                        while (! loginUsers.hasUser(player)) {
                            if (rejectUsers.hasUser(player)) {
                                User rejected = rejectUsers.getUser(player.getUuid());
                                if (rejected.getRejectReason() == null) {
                                    LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().asString() + "\", because player are not white-listed");
                                } else {
                                    LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().asString() + "\"");
                                }
                                disc(rejected.getRejectReason() == null ? new TranslatableText("multiplayer.disconnect.not_whitelisted") : rejected.getRejectReason());

                                rejectUsers.removeUser(player);

                                LOGGER.info("rejected nano: " + nano + " (" + player.getName().asString() + ")");
                                return;
                            } else {
                                if (TimeUtil.processMillion(waiting) > loginCheckTimeLimit) {
                                    disc(new LiteralText("server enabled ModMdo secure module, please login with ModMdo"));
                                    LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().asString() + "\", because player not login with ModMdo");

                                    LOGGER.info("rejected nano: " + nano + " (" + player.getName().asString() + ")");
                                    return;
                                }
                            }

                            if (! connection.isOpen()) {
                                break;
                            }

                            EntrustExecution.tryTemporary(() -> TimeUtil.barricade(15));
                        }
                    }

                    try {
                        try {
                            if (connection.isOpen()) {
                                callOnConnect(player);
                                LOGGER.info("accepted nano: " + nano + " (" + player.getName().asString() + ")");

                                updateWhitelistNames(server, true);
                                updateTemporaryWhitelistNames(server, true);
                                updateModMdoConnectionsNames(server);
                            } else {
                                LOGGER.info("expired nano: " + nano + " (" + player.getName().asString() + ")");
                            }
                        } catch (Exception e) {
                            if (! server.isHost(player.getGameProfile())) {
                                LOGGER.info("player " + player.getName().asString() + " lost status synchronize");

                                disc(new LiteralText("lost status synchronize, please connect again"));
                            } else {
                                LOGGER.info("player " + player.getName().asString() + " lost status synchronize, but will not be process");
                            }
                        }
                    } catch (Exception e) {

                    }
                }).start();
            } else {
                serverLogin.login(player.getName().getString(), player.getUuid().toString(), configCached.getConfigString("identifier"), MODMDO_VERSION);

                callOnConnect(player);
            }
        } else {
            callOnConnect(player);
        }
    }

    public void callOnConnect(ServerPlayerEntity player) {
        this.server.getPlayerManager().onPlayerConnect(this.connection, player);
    }

    public void disc(Text reason) {
        this.connection.send(new DisconnectS2CPacket(reason));
        this.connection.disconnect(reason);
    }

    @Inject(method = "onKey", at = @At("HEAD"))
    public void onKey(LoginKeyC2SPacket packet, CallbackInfo ci) {
        authing = true;
    }

    @Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
    public void disconnect(Text reason, CallbackInfo ci) {
        if (authing && config.getConfigBoolean("compatible_online_mode")) {
            preReject = true;
            ci.cancel();
        }
    }
}
