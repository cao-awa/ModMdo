package com.github.cao.awa.modmdo.mixins.server.login;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.entity.player.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.authlib.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.network.packet.s2c.login.*;
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

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer server, ClientConnection connection, CallbackInfo ci) {
        int radix = 4;
        for (byte b : this.nonce) {
            NONCE[NONCE.length - radix--] = b;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (doCheckModMdo) {
            acceptPlayer();
            doCheckModMdo = false;
        }
    }

    @Shadow
    public abstract void acceptPlayer();

    @Shadow @Final private byte[] nonce;

    /**
     * @author 草awa
     * @author 草二号机
     */
    @Redirect(method = "addToServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void onPlayerConnect(PlayerManager manager, ClientConnection connection, ServerPlayerEntity player) {
        if (SharedVariables.isActive()) {
            if (player == null)
                return;

            String name = EntityUtil.getName(player);

            TRACKER.info("player " + name + " trying join server");

            if (config.getConfigBoolean("modmdo_whitelist")) {
                Receptacle<Boolean> isDoneOnlineMode = new Receptacle<>(false);

                if (server.isHost(player.getGameProfile())) {
                    serverLogin.login(name, PlayerUtil.getUUID(player).toString(), staticConfig.getConfigString("identifier"), String.valueOf(MODMDO_VERSION), null, null);

                    manager.onPlayerConnect(connection, player);
                } else {
                    int loginCheckTimeLimit = config.getConfigInt("checker_time_limit");

                    loginTimedOut.put(name, TimeUtil.millions() + loginCheckTimeLimit);

                    if (server.isOnlineMode()) {
                        if (! (profile == null || profile.getId() == null) && ! preReject) {
                            if (config.getConfigBoolean("compatible_online_mode")) {
                                EntrustExecution.tryTemporary(() -> {
                                    serverLogin.loginUsingYgg(name, profile.getId().toString());
                                    manager.onPlayerConnect(connection, player);
                                    isDoneOnlineMode.set(true);
                                }, ex -> {
                                    serverLogin.reject(name, profile.getId().toString(), "", TextUtil.translatable("multiplayer.disconnect.not_whitelisted").text());
                                });
                            }
                        }
                    }

                    ServerPlayNetworkHandler handler = new ServerPlayNetworkHandler(server, connection, player);
                    TRACKER.submit("Server send test packet: modmdo-connection", () -> {
                        handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(DATA_CHANNEL).writeString("modmdo-connection")));
                    });
                    TRACKER.submit("Server send test packet: old modmdo version test", () -> {
                        handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeVarInt(modmdoWhitelist ? 99 : 96)));
                    });

                    if (! isDoneOnlineMode.get()) {
                        TRACKER.info("Player " + name + " are not done online mode, will check again using modmdo");
                        TRACKER.submit("Server send login packet: modmdo login", () -> {
                            if (modmdoWhitelist) {
                                handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(CHECKING_CHANNEL).writeString(staticConfig.get("identifier"))));
                            } else {
                                handler.sendPacket(new CustomPayloadS2CPacket(SERVER_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(LOGIN_CHANNEL)));
                            }
                        });

                        CompletableFuture.runAsync(() -> {
                            while (TimeUtil.millions() < loginTimedOut.get(name)) {
                                TimeUtil.coma(10);
                            }
                            if (loginTimedOut.containsKey(name) && (connection.isOpen() || ! loginUsers.hasUser(name))) {
                                if (rejectUsers.hasUser(name)) {
                                    disc(rejectUsers.getUser(name).getMessage());

                                    rejectUsers.removeUser(name);
                                } else {
                                    disc(TextUtil.literal("You are failed login because too long did not received login request").text());
                                }
                            } else {
                                loginTimedOut.remove(name);
                            }
                        });
                    } else {
                        TRACKER.info("Player " + name + " are done online mode, will not check again using modmdo");
                    }
                }
            } else {
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

    @Redirect(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;)V"))
    public void helloModMdo(ClientConnection instance, Packet<?> packet) {
        instance.send(new LoginHelloS2CPacket("", this.server.getKeyPair().getPublic().getEncoded(), SharedVariables.NONCE));
    }

    @Redirect(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;nonce:[B"))
    public byte[] onKey(ServerLoginNetworkHandler instance) {
        return SharedVariables.NONCE;
    }
}
