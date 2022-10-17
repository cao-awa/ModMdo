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
     * 当相同的玩家在线时, 禁止重复创建玩家
     *
     * @param profile
     *         即将加入的玩家
     * @param cir
     *         callback
     * @author 草二号机
     */
    @Inject(method = "createPlayer", at = @At("HEAD"), cancellable = true)
    public void createPlayer(GameProfile profile, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        if (SharedVariables.enableRejectReconnect) {
            UUID uuid = PlayerUtil.getUUID(profile);
            for (ServerPlayerEntity player : this.players) {
                if (player.networkHandler.connection.getAddress() == null)
                    break;
                if (player.getUuid().equals(uuid)) {
                    if (loginUsers.hasUser(player)) {
                        SimpleCommandOperation.sendMessage(player, Translatable.translatable("login.dump.rejected"), false);
                    }
                    cir.setReturnValue(null);
                }
            }
        }
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"), cancellable = true)
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (SharedVariables.extras != null && SharedVariables.extras.isActive(SharedVariables.EXTRA_ID)) {
            SharedVariables.event.submit(new JoinServerEvent(player, connection, player.getPos(), SharedVariables.server));
        }

        if (! connection.isOpen()) {
            ci.cancel();
        }
    }

    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;savePlayerData(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void remove(PlayerManager instance, ServerPlayerEntity player) {
        EntrustExecution.tryTemporary(() -> {
            if ((loginUsers.hasUser(player) && ! banned.containsIdentifier(loginUsers.getUser(player.getUuid()).getIdentifier())) || force.contains(player) || player.networkHandler.getConnection().getAddress() == null) {
                force.remove(player);
            }
            savePlayerData(player);
        }, Throwable::printStackTrace);
    }

    @Shadow
    protected abstract void savePlayerData(ServerPlayerEntity player);

    @Shadow public abstract MinecraftServer getServer();

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
    public void sendPacket(ServerPlayNetworkHandler instance, Packet<?> packet) {
        instance.sendPacket(packet);
    }

//    @Inject(method = "getAdvancementTracker", at = @At("HEAD"), cancellable = true)
//    public void optimizeAdvancementTracker(ServerPlayerEntity player, CallbackInfoReturnable<PlayerAdvancementTracker> cir) {
//        cir.setReturnValue(optimizeAdvancementTracker(player, getServer().getPlayerManager()));
//    }
//
//    public PlayerAdvancementTracker optimizeAdvancementTracker(ServerPlayerEntity player, PlayerManager manager) {
//        UUID uuid = player.getUuid();
//        long start = TimeUtil.millions();
//        TRACKER.info("Loading advancement tracker for " + uuid);
//        PlayerAdvancementTracker playerAdvancementTracker = advancementTrackerCaches.get(uuid.toString());
//        if (playerAdvancementTracker == null) {
//            TRACKER.info("Initializing advancement tracker for " + uuid);
//            File file = this.server.getSavePath(WorldSavePath.ADVANCEMENTS).toFile();
//            File file2 = new File(file, uuid + ".json");
//            playerAdvancementTracker = new PlayerAdvancementTracker(this.server.getDataFixer(), manager, this.server.getAdvancementLoader(), file2, player);
//            advancementTrackerCaches.put(uuid.toString(), playerAdvancementTracker);
//        } else {
//            TRACKER.info("Loading cached advancement tracker");
//            TRACKER.info("Updating advancements...");
//            playerAdvancementTracker.reload(null);
//        }
//
//        playerAdvancementTracker.setOwner(player);
//        TRACKER.info("Loaded advancement tracker for " + uuid + ", done in " + TimeUtil.processMillion(start) + "ms");
//        return playerAdvancementTracker;
//    }
//
//    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;clearCriteria()V"))
//    public void remove(PlayerAdvancementTracker instance) {
//
//    }
}
