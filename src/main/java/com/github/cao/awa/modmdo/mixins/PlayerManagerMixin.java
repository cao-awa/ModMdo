package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.SimpleCommandOperation;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    @Shadow protected abstract void savePlayerData(ServerPlayerEntity player);

    /**
     * 当相同的玩家在线时, 禁止重复创建玩家
     * 几乎解决了玩家异地登录下线的问题
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
                UUID uuid = PlayerEntity.getUuidFromProfile(profile);
                for (ServerPlayerEntity player : this.players) {
                    if (player.networkHandler.connection.getAddress() == null)
                        break;
                    if (player.getUuid().equals(uuid)) {
                        if (loginUsers.hasUser(player)) {
                            SimpleCommandOperation.sendMessage(player, new TranslatableText("login.dump.rejected"), false);
                        }
                        cir.setReturnValue(null);
                        cir.cancel();
                    }
                }
            }
        }
    }

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (SharedVariables.extras != null && SharedVariables.extras.isActive(SharedVariables.EXTRA_ID)) {
            EntrustExecution.tryFor(SharedVariables.modmdoConnections, processor -> processor.sendPlayerJoin(player.getName().asString()));
            SharedVariables.event.submitJoinServer(player, connection, player.getPos(), SharedVariables.server);
        }
    }

    @Redirect(method = "remove", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;savePlayerData(Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    public void remove(PlayerManager instance, ServerPlayerEntity player) {
        if (loginUsers.hasUser(player) && !banned.containsIdentifier(loginUsers.getUser(player.getUuid()).getIdentifier()) || force.contains(player)) {
            System.out.println("???");
            force.remove(player);
            savePlayerData(player);
        }
    }
}