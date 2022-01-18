package com.github.zhuaidadaya.modMdo.mixins;

import io.netty.buffer.Unpooled;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {

    @Shadow
    @Final
    public ClientConnection connection;

    @Shadow
    @Final
    MinecraftServer server;

    /**
     * 如果玩家为null, 则拒绝将玩家添加进服务器
     * (因为其他地方有cancel, 所以可能null)
     *
     * @param player
     *         玩家
     *
     * @author 草awa
     * @author 草二号机
     * @reason
     */
    @Overwrite
    private void addToServer(ServerPlayerEntity player) {
        if(player == null)
            return;

        new Thread(() -> {
            ServerPlayNetworkHandler serverPlayNetworkHandler = new ServerPlayNetworkHandler(this.server, connection, player);

            serverPlayNetworkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(enableEncryptionToken ? 99 : 96)));

            long waiting = System.currentTimeMillis();
            while(! loginUsers.hasUser(player)) {
                if(System.currentTimeMillis() - waiting > 1500) {
                    if(rejectUsers.hasUser(player)) {
                        connection.send(new DisconnectS2CPacket(new LiteralText("obsolete token, please update")));
                    } else {
                        connection.send(new DisconnectS2CPacket(new LiteralText("this server enabled ModMdo checker, please login with token")));
                    }
                    connection.disconnect(new LiteralText("failed to login server"));
                    try {
                        rejectUsers.removeUser(player);
                    } catch (Exception e) {

                    }
                    return;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {

                }
            }

            this.server.getPlayerManager().onPlayerConnect(this.connection, player);
        }).start();
    }
}