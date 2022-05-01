package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.MessageType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

/**
 * TAG:DRT|SKP|VSD
 * 这个tag用于注明这是有版本差异的
 * 存在这个tag时不会直接从其他正在开发的部分复制
 * 而是手动替换
 * TAG:
 * DRT(Don't Replace It)
 * SKP(Skip)
 * VSD(Version Difference)
 * <p>
 * 手动替换检测: 1.17.x
 */
@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Shadow
    @Final
    public ClientConnection connection;

    @Shadow
    @Final
    private MinecraftServer server;

    @Shadow
    protected abstract boolean isHost();

    /**
     * 解析玩家发送的数据包, 如果identifier为 <code>modmdo:token</code> 则检查token<br>
     * token正确就加入loginUsers中, 加入就算放行了<br>
     * <br>
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
        try {
            Identifier channel = EntrustParser.tryCreate(packet::getChannel, new Identifier(""));

            PacketByteBuf packetByteBuf = EntrustParser.trying(() -> new PacketByteBuf(packet.getData().copy()));

            String data1 = EntrustParser.tryCreate(packetByteBuf::readString, "");
            String data2 = EntrustParser.tryCreate(packetByteBuf::readString, "");
            String data3 = EntrustParser.tryCreate(packetByteBuf::readString, "");
            String data4 = EntrustParser.tryCreate(packetByteBuf::readString, "");

            if(channel.equals(LOGIN)) {
                if(modMdoType == ModMdoType.SERVER) {
                    serverLogin.login(data1, data2, data3,data4);
                }
            }

            ci.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在玩家退出游戏后去除放行状态, 下一次进入也需要重传token
     *
     * @param reason
     *         移除信息
     *
     * @author 草awa
     * @author 草二号机
     * @author zhuaidadaya
     * @reason
     */
    @Overwrite
    public void onDisconnected(Text reason) {
        forceStopTokenCheck = true;

        new Thread(() -> {
            Thread.currentThread().setName("ModMdo accepting");

            if((! rejectUsers.hasUser(player) || loginUsers.hasUser(player)) & server.getPlayerManager().getPlayer(player.getUuid()) != null) {
                LOGGER.info("{} lost connection: {}", this.player.getName().getString(), reason.getString());
                this.server.getPlayerManager().broadcast((new TranslatableText("multiplayer.player.left", this.player.getDisplayName())).formatted(Formatting.YELLOW), MessageType.SYSTEM, Util.NIL_UUID);
                this.server.forcePlayerSampleUpdate();
                this.server.getPlayerManager().remove(this.player);
                this.player.onDisconnect();
                this.player.getTextStream().onDisconnect();
            }

            if(rejectUsers.hasUser(player)) {
                rejectUsers.removeUser(player);
            }

            serverLogin.logout(player);

            if(this.isHost()) {
                LOGGER.info("Stopping singleplayer server as player logged out");
                this.server.stop(false);
            }

            forceStopTokenCheck = false;
        }).start();
    }

    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true)
    private void executeCommand(String input, CallbackInfo ci) {
        LOGGER.info(player.getName().asString() + "(" + player.getUuid().toString() + ") run the command: " + input);
        sendFollowingMessage(server.getPlayerManager(), new TranslatableText("player.run.command.try", player.getName().asString(), input), "run_command_follow");
        if(! loginUsers.hasUser(player) & modmdoWhiteList) {
            LOGGER.info("rejected command request: not login user");
            sendFollowingMessage(server.getPlayerManager(), new TranslatableText("player.run.command.rejected.without.login", player.getName().asString()), "run_command_follow");
            ci.cancel();
        }
        sendFollowingMessage(server.getPlayerManager(), new TranslatableText("player.run.command", player.getName().asString(), input), "run_command_follow");
    }
}