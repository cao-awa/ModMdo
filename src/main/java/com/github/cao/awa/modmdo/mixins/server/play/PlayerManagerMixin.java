package com.github.cao.awa.modmdo.mixins.server.play;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.entity.player.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.entity.*;
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

    @Shadow protected abstract void savePlayerData(ServerPlayerEntity player);

    /**
     * 当相同的玩家在线时, 禁止重复创建玩家
     *
     * @param profile
     *         即将加入的玩家
     * @param cir
     *         callback
     * @author 草二号机
     */
    @Inject(method = "createPlayer", at = @At("HEAD"))
    public void createPlayer(GameProfile profile, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        if (SharedVariables.isActive()) {
            if (SharedVariables.enableRejectReconnect) {
                UUID uuid = PlayerUtil.getId(profile);
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
    }

    @Shadow @Final private Map<UUID, ServerPlayerEntity> playerMap;

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (SharedVariables.extras != null && SharedVariables.extras.isActive(SharedVariables.EXTRA_ID)) {
            EntrustExecution.tryFor(SharedVariables.modmdoConnections, processor -> processor.sendPlayerJoin(EntityUtil.getName(player)));
            SharedVariables.event.submit(new JoinServerEvent(player, connection, player.getPos(), SharedVariables.server));
        }
    }

    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;savePlayerData(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void remove(PlayerManager instance, ServerPlayerEntity player) {
        EntrustExecution.tryTemporary(() -> {
            if (loginUsers.hasUser(player) && !banned.containsIdentifier(loginUsers.getUser(player.getUuid()).getIdentifier()) || force.contains(player) || player.networkHandler.getConnection().getAddress() == null) {
                force.remove(player);
                savePlayerData(player);
            }
        }, Throwable::printStackTrace);
    }
}
