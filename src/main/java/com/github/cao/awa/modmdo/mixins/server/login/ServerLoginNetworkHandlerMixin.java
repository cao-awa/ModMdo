package com.github.cao.awa.modmdo.mixins.server.login;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.login.network.*;
import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.digger.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.entity.player.*;
import com.github.cao.awa.modmdo.utils.packet.sender.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.authlib.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin implements ServerLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoServerLoginHandler");

    private static final int loginLimit = 300;
    private static final Literal DISCONNECT_DDOS = TextUtil.literal("Server are under ddos attack, please login later");
    private static final Literal NOTIFY_DDOS = TextUtil.literal("Server are under ddos attack, unable to login again if logout");
    private static final ReentrantLock lock = new ReentrantLock();
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
    private boolean afterOnlineMode = false;
    private GameProfile profileOld;
    @Shadow
    @Final
    private byte[] nonce;

    private ServerPacketSender sender;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(MinecraftServer server, ClientConnection connection, CallbackInfo ci) {
        sender = new ServerPacketSender(
                connection,
                SERVER_CHANNEL
        );

        int radix = 4;
        for (byte b : this.nonce) {
            MODMDO_NONCE[MODMDO_NONCE.length - radix--] = b;
        }

        //        if (    // Attack protection trigger.
        //                currentLogin >= loginLimit ||
        //                // Debug code, will not happen in using.
        //                serverUnderDdosAttack.get()) {
        //            lock.lock();
        //            connection.setPacketListener(new UnderAttackHandler(connection));
        //            connection.send(new LoginDisconnectS2CPacket(DISCONNECT_DDOS.text()));
        //            connection.disconnect(DISCONNECT_DDOS.text());
        //            if (ddosRecording == null) {
        //                DdosAttackRecorder.LOGGER.warn("Modmdo detected a maybe ddos attack, protection measures are enabled");
        //                serverUnderDdosAttack.set(true);
        //                ddosRecording = new DdosAttackRecorder(TimeUtil.millions());
        //                SharedVariables.sendMessageToAllPlayer(
        //                        server,
        //                        NOTIFY_DDOS.text(),
        //                        false
        //                );
        //                ddosAttackRecorders.add(ddosRecording);
        //            } else {
        //                ddosRecording.occurs();
        //            }
        //            lock.unlock();
        //        } else {
        //            currentLogin++;
        //        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (afterOnlineMode) {
            acceptPlayer();
            afterOnlineMode = false;
        }
    }

    @Shadow
    public abstract void acceptPlayer();

    /**
     * @author cao_awa
     * @author 草二号机
     */
    @Redirect(method = "addToServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;onPlayerConnect(Lnet/minecraft/network/ClientConnection;Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void onPlayerConnect(PlayerManager manager, ClientConnection connection, ServerPlayerEntity player) {
        if (player == null || serverUnderDdosAttack.get()) {
            return;
        }

        String name = EntityUtil.getName(player);

        LOGGER.info(
                "player {} trys join server",
                name
        );

        if (handleBanned(player)) {
            Certificate certificate = bans.get(EntityUtil.getName(player));
            if (certificate instanceof TemporaryCertificate temporary) {
                String remaining = temporary.formatRemaining();
                disc(textFormatService.format(
                                                new com.github.cao.awa.modmdo.lang.Dictionary(certificate.getLanguage()),
                                                "multiplayer.disconnect.banned-time-limited",
                                                remaining
                                        )
                                      .text());
                LOGGER.info(
                        "Player {} has been banned form server",
                        PlayerUtil.getName(player)
                );
            } else {
                disc(textFormatService.format(
                                                new Dictionary(certificate.getLanguage()),
                                                "multiplayer.disconnect.banned-indefinite"
                                        )
                                      .text());
                LOGGER.info(
                        "Player {} has been banned form server",
                        PlayerUtil.getName(player)
                );
            }
        } else {
            if (config.getBoolean("modmdo_whitelist")) {
                Receptacle<Boolean> isDoneOnlineMode = new Receptacle<>(false);

                if (server.isHost(player.getGameProfile())) {
                    serverLogin.login(
                            name,
                            PlayerUtil.getUUID(player)
                                      .toString(),
                            staticConfig.getString("identifier"),
                            MODMDO_VERSION_NAME,
                            null,
                            null
                    );

                    manager.onPlayerConnect(
                            connection,
                            player
                    );
                } else {
                    int loginCheckTimeLimit = config.getInt("checker_time_limit");

                    loginTimedOut.put(
                            name,
                            TimeUtil.millions() + loginCheckTimeLimit
                    );

                    if (server.isOnlineMode()) {
                        if (! (profile == null || profile.getId() == null) && ! preReject) {
                            if (config.getBoolean("compatible_online_mode")) {
                                EntrustEnvironment.trys(
                                        () -> {
                                            if (serverLogin.loginUsingYgg(
                                                    name,
                                                    profile.getId()
                                                           .toString()
                                            )) {
                                                manager.onPlayerConnect(
                                                        connection,
                                                        player
                                                );
                                                isDoneOnlineMode.set(true);
                                            }
                                        }
                                );
                            }
                        }
                    }

                    if (isDoneOnlineMode.get()) {
                        return;
                    }

                    LOGGER.debug("Server send test packet: old modmdo version test");

                    // Processing login packet need uses ServerPlayNetworkHandler
                    // Let packet handler of ClientConnection become this
                    new ModMdoLoginNetworkHandler(
                            server,
                            connection,
                            player
                    );

                    sender.chanel(SERVER_CHANNEL)
                          .custom()
                          .var(modmdoWhitelist ? 99 : 96)
                          .send();

                    LOGGER.debug("Server send login packet: modmdo login");
                    EntrustEnvironment.trys(() -> {
                        if (modmdoWhitelist) {
                            String identifier = staticConfig.getString("identifier");
                            sender.custom()
                                  .write(CHECKING_CHANNEL)
                                  .write(EntrustEnvironment.get(
                                          () -> MessageDigger.digest(
                                                  identifier,
                                                  MessageDigger.Sha3.SHA_512
                                          ),
                                          identifier
                                  ))
                                  .send();
                        } else {
                            sender.custom()
                                  .write(LOGIN_CHANNEL)
                                  .send();
                        }
                    });

                    CompletableFuture.runAsync(() -> {
                        boolean skip = false;
                        while (TimeUtil.millions() < loginTimedOut.get(name)) {
                            TimeUtil.coma(10);
                            if (loginUsers.hasUser(name) || !connection.isOpen()) {
                                skip = true;
                                break;
                            }
                        }
                        if (skip) {
                            loginTimedOut.remove(name);
                        }
                        if (loginTimedOut.containsKey(name)) {
                            if (rejectUsers.hasUser(name)) {
                                disc(rejectUsers.getUser(name)
                                                .getMessage());

                                rejectUsers.removeUser(name);
                            } else {
                                disc(TextUtil.literal("You are failed login because too long did not received login request")
                                             .text());
                            }
                        }
                        loginTimedOut.remove(name);
                    });
                }
            } else {
                serverLogin.login(
                        name,
                        PlayerUtil.getUUID(player)
                                  .toString(),
                        "",
                        "",
                        "",
                        ""
                );
                manager.onPlayerConnect(
                        connection,
                        player
                );
            }
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
        if (authing && config.getBoolean("compatible_online_mode") && config.getBoolean("modmdo_whitelist")) {
            profile = profileOld;
            preReject = true;
            afterOnlineMode = true;
            ci.cancel();
        }
    }

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    public void onDisconnected0(Logger instance, String s, Object o1, Object o2) {
        if (serverUnderDdosAttack.get()) {
            return;
        }
        instance.info(
                s,
                o1,
                o2
        );
    }

    @Redirect(method = "onHello", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;)V"))
    public void helloModMdo(ClientConnection connection, Packet<?> packet) {
        sender.swap(connection)
              .hello(
                      "",
                      this.server.getKeyPair()
                                 .getPublic()
                                 .getEncoded(),
                      SharedVariables.MODMDO_NONCE
              )
              .send();
    }

    @Redirect(method = "onKey", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;nonce:[B"))
    public byte[] onKey(ServerLoginNetworkHandler instance) {
        return SharedVariables.MODMDO_NONCE;
    }
}
