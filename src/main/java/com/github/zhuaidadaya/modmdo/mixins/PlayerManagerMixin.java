package com.github.zhuaidadaya.modmdo.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow
    @Final
    private List<ServerPlayerEntity> players;

    /**
     * 当相同的玩家在线时, 禁止重复创建玩家
     * 几乎解决了玩家异地登录下线的问题
     *
     * @author 草二号机
     * @param profile 即将加入的玩家
     * @param cir callback
     */
    @Inject(method = "createPlayer",at = @At("HEAD"))
    public void createPlayer(GameProfile profile, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        if(enableRejectReconnect) {
            UUID uuid = PlayerEntity.getUuidFromProfile(profile);
            for(ServerPlayerEntity player : this.players) {
                if(player.networkHandler.connection.getAddress() == null)
                    break;
                if(player.getUuid().equals(uuid)) {
                    if(loginUsers.hasUser(player)) {
                        if(getFeatureCanUse(15, player)) {
                            sendMessageToPlayer(player,new TranslatableText("login.dump.rejected"),false);
                        }
                    }
                    cir.cancel();
                }
            }
        }
    }
}
