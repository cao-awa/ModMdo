package com.github.cao.awa.modmdo.mixins.player;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.entity.player.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.authlib.*;
import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    @Final
    private List<ServerPlayerEntity> players;
    @Shadow
    @Final
    private Map<UUID, ServerPlayerEntity> playerMap;
    @Shadow
    @Final
    private MinecraftServer server;

    /**
     * Do not repetitive creating player
     *
     * @param profile
     *         Player profile
     * @param cir
     *         callback
     * @author 草二号机
     */
    @Inject(method = "createPlayer", at = @At("HEAD"), cancellable = true)
    public void createPlayer(GameProfile profile, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        if (SharedVariables.enableRejectReconnect) {
            UUID uuid = PlayerUtil.getUUID(profile);
            ServerPlayerEntity player = getPlayer(uuid);
            if (player != null) {
                // Compatible for carpet dummy player
                if (player.networkHandler.connection.getAddress() != null) {
                    if (loginUsers.hasUser(player)) {
                        SimpleCommandOperation.sendMessage(
                                player,
                                Translatable.translatable("login.dump.rejected"),
                                false
                        );
                    }
                    cir.setReturnValue(null);
                }
            }
        }
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"), cancellable = true)
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (SharedVariables.extras != null && SharedVariables.extras.isActive(SharedVariables.EXTRA_ID)) {
            SharedVariables.event.submit(new JoinServerEvent(
                    player,
                    connection,
                    player.getPos(),
                    SharedVariables.server
            ));
        }

        if (connection.isOpen()) {
            connections.add(connection);
        } else {
            ci.cancel();
        }
    }

    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;savePlayerData(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void remove(PlayerManager instance, ServerPlayerEntity player) {
        EntrustEnvironment.trys(
                () -> {
                    if ((loginUsers.hasUser(player) && ! bans.containsIdentifier(loginUsers.getUser(player.getUuid())
                                                                                           .getIdentifier())) || FORCE.contains(player) || player.networkHandler.getConnection()
                                                                                                                                                                  .getAddress() == null) {
                        FORCE.remove(player);
                    }
                    savePlayerData(player);
                },
                Throwable::printStackTrace
        );
    }

    @Shadow
    protected abstract void savePlayerData(ServerPlayerEntity player);

    @Shadow
    public abstract MinecraftServer getServer();

    @Shadow @Nullable public abstract ServerPlayerEntity getPlayer(String name);

    @Shadow @Nullable public abstract ServerPlayerEntity getPlayer(UUID uuid);

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
    public void sendPacket(ServerPlayNetworkHandler instance, Packet<?> packet) {
        instance.sendPacket(packet);
    }
}
