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
import net.minecraft.text.TranslatableText;
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
            Thread.currentThread().setName("ModMdo accepting");

            long waiting = System.currentTimeMillis();

            try {
                new ServerPlayNetworkHandler(server, connection, player).sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(enableEncryptionToken ? 99 : 96)));

                sendFollowingMessage(server.getPlayerManager(), new TranslatableText("player.login.try", player.getName().asString()), "joinServer");
            } catch (Exception e) {

            }

            if(enableEncryptionToken) {
                while(! loginUsers.hasUser(player)) {
                    if(rejectUsers.hasUser(player)) {
                        connection.send(new DisconnectS2CPacket(new LiteralText("obsolete token, please update")));
                        LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().asString() + "\", because player provided a bad token");
                        sendFollowingMessage(server.getPlayerManager(), new TranslatableText("player.login.rejected.bad.token", player.getName().asString()), "joinServer");
                        waiting = 0;
                    }
                    if(System.currentTimeMillis() - waiting > 3000) {
                        if(! rejectUsers.hasUser(player)) {
                            connection.send(new DisconnectS2CPacket(new LiteralText("server enabled ModMdo secure module, please login with token")));
                            LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().asString() + "\", because player not login with ModMdo");
                            sendFollowingMessage(server.getPlayerManager(), new TranslatableText("player.login.rejected.without.modmdo", player.getName().asString()), "joinServer");
                        }
                        connection.disconnect(new LiteralText("failed to login server"));
                        try {
                            rejectUsers.removeUser(player);
                        } catch (Exception e) {

                        }
                        return;
                    }

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {

                    }
                }

                server.getPlayerManager().onPlayerConnect(connection, player);

                player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(107).writeString(servers.toJSONObject().toString())));
            } else {
                server.getPlayerManager().onPlayerConnect(connection, player);

                player.networkHandler.sendPacket(new CustomPayloadS2CPacket(modMdoServerChannel, new PacketByteBuf(Unpooled.buffer()).writeVarInt(107).writeString(servers.toJSONObject().toString())));
            }
        }).start();
    }
}