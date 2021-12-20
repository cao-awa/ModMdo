package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.modMdo.type.ModMdoType;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    @Final
    public ClientConnection connection;

    /**
     * 解析玩家发送的数据包, 如果identifier为 <code>modmdo:token</code> 则检查token
     * token正确就加入loginUsers中, 加入就算放行了
     *
     * @param packet
     *         客户端发送的数据包
     * @param ci
     *         callback
     *
     * @author 草awa
     * @author 草二号机
     * @author zhuaidadaya
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        Identifier channel = new Identifier("");
        try {
            channel = packet.getChannel();
        } catch (Exception e) {

        }

        PacketByteBuf packetByteBuf = null;
        try {
            packetByteBuf = new PacketByteBuf(packet.getData().copy());
        } catch (Exception e) {
        }


        String data1 = "";
        try {
            data1 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        String data2 = "";
        try {
            data2 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        String data3 = "";
        try {
            data3 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        String data4 = "";
        try {
            data4 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        String data5 = "";
        try {
            data5 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        if(enableEncryptionToken & modMdoType == ModMdoType.SERVER) {
            serverLogin.login(channel,data1,data2,data3,data4,data5);
        }

        ci.cancel();
    }

    /**
     * 在玩家退出游戏后去除放行状态, 下一次进入也需要重传token
     *
     * @param reason
     *         移除信息
     * @param ci
     *         callback
     *
     * @author 草awa
     * @author 草二号机
     * @author zhuaidadaya
     */
    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        if(enableEncryptionToken) {
            serverLogin.logout(player);
        }
    }

    /**
     * 不登入不给移动
     *
     * @param packet
     *         移动请求
     * @param ci
     *         callback
     *
     * @author 草二号机
     */
    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    public void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
            ci.cancel();
        }
    }

    /**
     * 不登入不给移动
     *
     * @param packet
     *         移动请求
     * @param ci
     *         callback
     *
     * @author 草二号机
     */
    @Inject(method = "onVehicleMove", at = @At("HEAD"), cancellable = true)
    public void onVehicleMove(VehicleMoveC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
            ci.cancel();
        }
    }

    /**
     * 不登入不给输入(
     *
     * @param packet
     *         输入请求
     * @param ci
     *         callback
     *
     * @author 草二号机
     */
    @Inject(method = "onPlayerInput", at = @At("HEAD"), cancellable = true)
    public void onPlayerInput(PlayerInputC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
            ci.cancel();
        }
    }

    /**
     * 不登入不给传送
     *
     * @param x
     *         x
     * @param y
     *         y
     * @param z
     *         z
     * @param yaw
     *         yaw
     * @param pitch
     *         pitch
     * @param flags
     *         flags
     * @param shouldDismount
     *         should dismount
     * @param ci
     *         callback
     *
     * @author 草二号机
     */
    @Inject(method = "requestTeleport(DDDFFLjava/util/Set;Z)V", at = @At("HEAD"), cancellable = true)
    public void requestTeleport(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags, boolean shouldDismount, CallbackInfo ci) {
        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
            ci.cancel();
        }
    }

    /**
     * 不登入不给使用命令
     *
     * @param input
     *         命令
     * @param ci
     *         callback
     *
     * @author 草二号机
     */
    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true)
    private void executeCommand(String input, CallbackInfo ci) {
        LOGGER.info(player.getName().asString() + "(" + player.getUuid().toString() + ") run the command: " + input);
        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
            LOGGER.info("rejected command request: not login user");
            ci.cancel();
        }
    }

    //    /**
    //     * 不登入不给保持live状态
    //     *
    //     * @author 草二号机
    //     *
    //     * @param packet live包
    //     * @param ci callback
    //     */
    //    @Inject(method = "onKeepAlive", at = @At("HEAD"), cancellable = true)
    //    public void onKeepAlive(KeepAliveC2SPacket packet, CallbackInfo ci) {
    //        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
    //            ci.cancel();
    //        }
    //    }

    /**
     * 草
     *
     * @param packet
     *         packet
     * @param ci
     *         callback
     *
     * @author 草二号机
     */
    @Inject(method = "onSpectatorTeleport", at = @At("HEAD"), cancellable = true)
    public void onSpectatorTeleport(SpectatorTeleportC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
            ci.cancel();
        }
    }

    /**
     * 不登入不给于物品交互
     *
     * @param packet
     *         packet
     * @param ci
     *         callback
     *
     * @author 草二号机
     */
    @Inject(method = "onPlayerInteractItem", at = @At("HEAD"), cancellable = true)
    public void onPlayerInteractItem(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
            ci.cancel();
        }
    }

    /**
     * 不登入不给于实体交互
     *
     * @param packet
     *         packet
     * @param ci
     *         callback
     *
     * @author 草二号机
     */
    @Inject(method = "onPlayerInteractEntity", at = @At("HEAD"), cancellable = true)
    public void onPlayerInteractEntity(PlayerInteractEntityC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
            ci.cancel();
        }
    }

    /**
     * 不登入不给于方块交互
     *
     * @param packet
     *         packet
     * @param ci
     *         callback
     *
     * @author 草二号机
     */
    @Inject(method = "onPlayerInteractBlock", at = @At("HEAD"), cancellable = true)
    public void onPlayerInteractBlock(PlayerInteractBlockC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player) & enableEncryptionToken) {
            ci.cancel();
        }
    }
}