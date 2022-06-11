package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.cao.awa.modmdo.utils.translate.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.authlib.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

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
        if (SharedVariables.isActive()) {
            if (player == null)
                return;

            if (server.isOnlineMode()) {
                if (profile == null || profile.getId() == null && preReject) {
                    return;
                }

                if (config.getConfigBoolean("compatible_online_mode")) {
                    EntrustExecution.tryTemporary(() -> {
                        serverLogin.loginUsingYgg(player.getName().getString(), profile.getId().toString());
                    }, () -> {
                        serverLogin.reject(player.getName().getString(), profile.getId().toString(), "", MutableText.of(TextUtil.translatable("multiplayer.disconnect.not_whitelisted")));
                    });
                }
            }

            if (! server.isHost(player.getGameProfile()) || modMdoType == ModMdoType.SERVER) {
                new Thread(() -> {
                    Thread.currentThread().setName("ModMdo accepting");
                    long nano = System.nanoTime();
                    LOGGER.info("nano " + nano + " (" + player.getName().getString() + ") trying join server");

                    long waiting = TimeUtil.millions();

                    int loginCheckTimeLimit = config.getConfigInt("checker_time_limit");

                    try {
                        ServerPlayNetworkHandler handler = new ServerPlayNetworkHandler(server, connection, player);
                        tracker.submit("Server send test packet: modmdo-connection", () -> {
                            handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("modmdo-connection")));
                        });
                        tracker.submit("Server send test packet: old modmdo version test", () -> {
                            handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeVarInt(modmdoWhitelist ? 99 : 96)));
                        });
                        tracker.submit("Server send login packet: modmdo login", () -> {
                            handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(modmdoWhitelist ? CHECKING_CHANNEL : LOGIN_CHANNEL)));
                        });
                        tracker.submit("Server send test packet: modmdo version suffix test", () -> {
                            handler.sendPacket(new CustomPayloadS2CPacket(SUFFIX_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SUFFIX_CHANNEL)));
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (modMdoType == ModMdoType.SERVER & modmdoWhitelist) {
                        while (! loginUsers.hasUser(player)) {
                            if (rejectUsers.hasUser(player)) {
                                User rejected = rejectUsers.getUser(player.getUuid());
                                if (rejected.getRejectReason() == null) {
                                    LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().getString() + "\", because player are not white-listed");
                                } else {
                                    LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().getString() + "\"");
                                }
                                disc(rejected.getRejectReason() == null ? MutableText.of(TextUtil.translatable("multiplayer.disconnect.not_whitelisted")) : rejected.getRejectReason());

                                rejectUsers.removeUser(player);

                                LOGGER.info("rejected nano: " + nano + " (" + player.getName().getString() + ")");
                                return;
                            } else {
                                if (TimeUtil.processMillion(waiting) > loginCheckTimeLimit) {
                                    disc(MutableText.of(new LiteralTextContent("server enabled ModMdo secure module, please login with ModMdo")));
                                    LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().getString() + "\", because player not login with ModMdo");

                                    LOGGER.info("rejected nano: " + nano + " (" + player.getName().getString() + ")");
                                    return;
                                }
                            }

                            if (! connection.isOpen()) {
                                break;
                            }

                            EntrustExecution.tryTemporary(() -> TimeUtil.barricade(10));
                        }
                    }

                    if (handleBanned(player)) {
                        Certificate ban = banned.get(player.getName().getString());
                        if (ban instanceof TemporaryCertificate temporary) {
                            String remaining = temporary.formatRemaining();
                            player.networkHandler.connection.send(new DisconnectS2CPacket(MutableText.of(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining))));
                            player.networkHandler.connection.disconnect(MutableText.of(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining)));
                        } else {
                            player.networkHandler.connection.send(new DisconnectS2CPacket(MutableText.of(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite"))));
                            player.networkHandler.connection.disconnect(MutableText.of(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite")));
                        }
                    }

                    try {
                        try {
                            if (connection.isOpen()) {
                                callOnConnect(player);
                                LOGGER.info("accepted nano: " + nano + " (" + player.getName().getString() + ")");

                                updateWhitelistNames(server, true);
                                updateTemporaryWhitelistNames(server, true);
                                updateModMdoConnectionsNames(server);
                                updateTemporaryBanNames(server, true);
                            } else {
                                LOGGER.info("expired nano: " + nano + " (" + player.getName().getString() + ")");
                            }
                        } catch (Exception e) {
                            if (! server.isHost(player.getGameProfile())) {
                                LOGGER.info("player " + player.getName().getString() + " lost status synchronize");

                                disc(MutableText.of(new LiteralTextContent("lost status synchronize, please connect again")));
                            } else {
                                LOGGER.info("player " + player.getName().getString() + " lost status synchronize, but will not be process");
                            }
                        }
                    } catch (Exception e) {

                    }
                }).start();
            } else {
                    serverLogin.login(player.getName().getString(), player.getUuid().toString(), staticConfig.getConfigString("identifier"), String.valueOf(MODMDO_VERSION));

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
