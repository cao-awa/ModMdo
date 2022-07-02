package com.github.cao.awa.modmdo.mixins.server.login;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.entity.player.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.times.*;
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

import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin implements ServerLoginPacketListener {
    @Shadow
    @Final
    public ClientConnection connection;
    @Shadow
    @Final
    MinecraftServer server;
    @Shadow
    @Nullable GameProfile profile;

    private boolean preReject = false;
    private boolean authing = false;
    private boolean doCheckModMdo = false;

    private GameProfile profileOld;

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (doCheckModMdo) {
            acceptPlayer();
            doCheckModMdo = false;
        }
    }

    @Shadow
    public abstract void acceptPlayer();

    /**
     * @author 草awa
     * @author 草二号机
     */
    @Redirect(method = "addToServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void onPlayerConnect(PlayerManager manager, ClientConnection connection, ServerPlayerEntity player) {
        if (SharedVariables.isActive()) {
            if (player == null)
                return;

            if (server.isOnlineMode()) {
                if (! (profile == null || profile.getId() == null) && ! preReject) {
                    if (config.getConfigBoolean("compatible_online_mode")) {
                        EntrustExecution.tryTemporary(() -> {
                            serverLogin.loginUsingYgg(EntityUtil.getName(player), profile.getId().toString());
                        }, ex -> {
                            serverLogin.reject(EntityUtil.getName(player), profile.getId().toString(), "", TextUtil.translatable("multiplayer.disconnect.not_whitelisted").text());
                        });
                    }
                }
            }

            if (! server.isHost(player.getGameProfile()) || modMdoType == ModMdoType.SERVER) {
                CompletableFuture.runAsync(() -> {
                    Thread.currentThread().setName("ModMdo accepting");
                    long nano = System.nanoTime();
                    TRACKER.info("nano " + nano + " (" + EntityUtil.getName(player) + ") trying join server");

                    long waiting = TimeUtil.millions();

                    int loginCheckTimeLimit = config.getConfigInt("checker_time_limit");

                    ServerPlayNetworkHandler handler = new ServerPlayNetworkHandler(server, connection, player);
                    TRACKER.submit("Server send test packet: modmdo-connection", () -> {
                        handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("modmdo-connection")));
                    });
                    TRACKER.submit("Server send test packet: old modmdo version test", () -> {
                        handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeVarInt(modmdoWhitelist ? 99 : 96)));
                    });
                    TRACKER.submit("Server send login packet: modmdo login", () -> {
                        if (modmdoWhitelist) {
                            handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(modmdoWhitelist ? CHECKING_CHANNEL : LOGIN_CHANNEL).writeString(staticConfig.get("identifier"))));
                        } else {
                            handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(modmdoWhitelist ? CHECKING_CHANNEL : LOGIN_CHANNEL)));
                        }
                    });

                    if (modMdoType == ModMdoType.SERVER && modmdoWhitelist) {
                        while (! loginUsers.hasUser(player)) {
                            if (rejectUsers.hasUser(player)) {
                                User rejected = rejectUsers.getUser(player.getUuid());
                                if (rejected.getMessage() == null) {
                                    TRACKER.warn("ModMdo reject a login request, player \"" + EntityUtil.getName(player) + "\", because player are not white-listed");
                                } else {
                                    TRACKER.warn("ModMdo reject a login request, player \"" + EntityUtil.getName(player) + "\"");
                                }
                                disc(rejected.getMessage() == null ? TextUtil.translatable("multiplayer.disconnect.not_whitelisted").text() : rejected.getMessage());

                                rejectUsers.removeUser(player);

                                TRACKER.info("Rejected nano: " + nano + " (" + EntityUtil.getName(player) + ")");
                                return;
                            } else {
                                if (TimeUtil.processMillion(waiting) > loginCheckTimeLimit) {
                                    disc(TextUtil.literal("server enabled ModMdo secure module, please login with ModMdo").text());
                                    TRACKER.warn("ModMdo reject a login request, player \"" + EntityUtil.getName(player) + "\", because player not login with ModMdo");

                                    TRACKER.info("Rejected nano: " + nano + " (" + EntityUtil.getName(player) + ")");
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
                        Certificate ban = banned.get(EntityUtil.getName(player));
                        if (ban instanceof TemporaryCertificate temporary) {
                            String remaining = temporary.formatRemaining();
                            player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining).text()));
                            player.networkHandler.connection.disconnect(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-time-limited", remaining).text());
                            TRACKER.info("Player " + PlayerUtil.getName(player) + " has been banned form server");
                        } else {
                            player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite").text()));
                            player.networkHandler.connection.disconnect(minecraftTextFormat.format(new Dictionary(ban.getLastLanguage()), "multiplayer.disconnect.banned-indefinite").text());
                            TRACKER.info("Player " + PlayerUtil.getName(player) + " has been banned form server");
                        }
                    }

                    try {
                        try {
                            if (connection.isOpen()) {
                                if (! loginUsers.hasUser(player)) {
                                    if (! config.getConfigBoolean("modmdo_whitelist")) {
                                        serverLogin.login(player.getName().getString(), player.getUuid().toString(), "", "", null, null);
                                    } else {
                                        disc(Translatable.translatable("multiplayer.disconnect.not_whitelisted").text());
                                    }
                                }

                                manager.onPlayerConnect(connection, player);
                                TRACKER.info("Accepted nano: " + nano + " (" + EntityUtil.getName(player) + ")");
                            } else {
                                TRACKER.info("Expired nano: " + nano + " (" + EntityUtil.getName(player) + ")");
                            }
                        } catch (Exception e) {
                            TRACKER.submit("Exception in join server", e);
                            if (! server.isHost(player.getGameProfile())) {
                                TRACKER.debug("player " + PlayerUtil.getName(player) + " lost status synchronize");

                                disc(TextUtil.literal("lost status synchronize, please connect again").text());
                            } else {
                                TRACKER.debug("player " + PlayerUtil.getName(player) + " lost status synchronize, but will not be process");
                            }
                        }
                    } catch (Exception e) {
                        TRACKER.submit("Exception in handle status sync lost", e);
                    }
                });
            } else {
                serverLogin.login(PlayerUtil.getName(player), PlayerUtil.getUUID(player).toString(), staticConfig.getConfigString("identifier"), String.valueOf(MODMDO_VERSION), null, null);

                manager.onPlayerConnect(connection, player);
            }
        } else {
            manager.onPlayerConnect(connection, player);
        }
    }

    public void disc(Text reason) {
        this.connection.send(new DisconnectS2CPacket(reason));
        this.connection.disconnect(reason);
    }

    @Inject(method = "onKey", at = @At("HEAD"))
    public void onKey(LoginKeyC2SPacket packet, CallbackInfo ci) {
        profileOld = profile;
        authing = true;
    }

    @Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
    public void disconnect(Text reason, CallbackInfo ci) {
        if (authing && config.getConfigBoolean("compatible_online_mode") && config.getConfigBoolean("modmdo_whitelist")) {
            profile = profileOld;
            preReject = true;
            doCheckModMdo = true;
            ci.cancel();
        }
    }

    @ModifyConstant(method = "onHello", constant = @Constant(stringValue = ""))
    public String helloModMdo(String constant) {
        return constant + ":ModMdo";
    }
}
