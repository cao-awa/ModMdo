package com.github.cao.awa.modmdo.mixins.server.play;

import com.github.cao.awa.modmdo.event.client.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.event.server.chat.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.times.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.slf4j.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("ModMdoServerAuthHandler");

    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    @Final
    public ClientConnection connection;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    private boolean waitingForKeepAlive;

    @Shadow
    private long lastKeepAliveTime;

    @Shadow
    private long keepAliveId;

    @Inject(method = "onDisconnected", at = @At("RETURN"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        serverLogin.logout(player);
        event.submit(new QuitServerEvent(
                player,
                connection,
                player.getPos(),
                server
        ));
    }

    public void disc(Text reason) {
        this.connection.send(new DisconnectS2CPacket(reason));
        this.connection.disconnect(reason);
    }

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void onDisconnected0(PlayerManager instance, Text message, MessageType type, UUID sender) {
        if (loginUsers.hasUser(player) || player.networkHandler.connection.getAddress() == null) {
            instance.broadcast(
                    message,
                    type,
                    sender
            );
        }
    }

    @Redirect(method = "onDisconnected", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
    public void onDisconnected0(Logger instance, String s, Object o1, Object o2) {
        if (serverUnderDdosAttack.get()) {
            return;
        }
        instance.info(s, o1, o2);
    }

    @Inject(method = "executeCommand", at = @At("HEAD"))
    private void executeCommand(String input, CallbackInfo ci) {
        LOGGER.info("'" + EntityUtil.getName(player) + "' run command: " + input);
    }

    @Inject(method = "onClientSettings", at = @At("HEAD"))
    private void onClientSettings(ClientSettingsC2SPacket packet, CallbackInfo ci) {
        event.submit(new ClientSettingEvent(
                player,
                packet,
                server
        ));
    }

    @Inject(method = "onChatMessage", at = @At("HEAD"))
    public void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        event.submit(new GameChatEvent(
                player,
                packet,
                server
        ));
    }

    @Inject(method = "onKeepAlive", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;disconnect(Lnet/minecraft/text/Text;)V"), cancellable = true)
    public void disconnect(KeepAliveC2SPacket packet, CallbackInfo ci) {
        if (loginUsers.hasUser(player)) {
            if (TimeUtil.processMillion(loginUsers.getUser(player)
                                                  .getLoginTime()) > 10000) {
                this.disc(TextUtil.translatable("disconnect.timeout")
                                  .text());
            } else {
                waitingForKeepAlive = false;
                lastKeepAliveTime = TimeUtil.millions();
                keepAliveId = lastKeepAliveTime;
            }

            ci.cancel();
        }
    }

    @Shadow
    protected abstract boolean isHost();
}
