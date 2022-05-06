package com.github.zhuaidadaya.modmdo.mixins;

import com.github.zhuaidadaya.modmdo.permission.PermissionLevel;
import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import com.github.zhuaidadaya.modmdo.utils.times.TimeUtil;
import com.github.zhuaidadaya.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin implements ServerLoginPacketListener {
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
     * @author 草awa
     * @author 草二号机
     * @reason
     */
    @Overwrite
    private void addToServer(ServerPlayerEntity player) {
        if (player == null)
            return;

        if (! server.isHost(player.getGameProfile())) {
            new Thread(() -> {
                Thread.currentThread().setName("ModMdo accepting");
                long nano = System.nanoTime();
                LOGGER.info("nano " + nano + " (" + player.getName().asString() + ") trying join server");

                long waiting = TimeUtil.millions();

                int loginCheckTimeLimit = EntrustParser.tryCreate(() -> config.getConfigInt("checker_time_limit"), 3000);
                boolean useModMdoWhitelist = EntrustParser.tryCreate(() -> config.getConfigBoolean("modmdo_whitelist"), false);

                try {
                    ServerPlayNetworkHandler handler = new ServerPlayNetworkHandler(server, connection, player);
                    handler.sendPacket(new CustomPayloadS2CPacket(SERVER, new PacketByteBuf(Unpooled.buffer()).writeVarInt(useModMdoWhitelist ? 99 : 96)));
                    handler.sendPacket(new CustomPayloadS2CPacket(SERVER, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(useModMdoWhitelist ? CHECKING : LOGIN)));
                } catch (Exception e) {

                }

                if (modMdoType == ModMdoType.SERVER & useModMdoWhitelist) {
                    while (! loginUsers.hasUser(player)) {
                        if (rejectUsers.hasUser(player)) {
                            User rejected = rejectUsers.getUser(player.getUuid());
                            connection.send(new DisconnectS2CPacket(rejected.getRejectReason() == null ? new TranslatableText("multiplayer.disconnect.not_whitelisted") : rejected.getRejectReason()));
                            if (rejected.getRejectReason() == null) {
                                LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().asString() + "\", because player are not white-listed");
                            } else {
                                LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().asString() + "\"");
                            }
                            connection.disconnect(new LiteralText("failed to login server"));

                            rejectUsers.removeUser(player);

                            LOGGER.info("rejected nano: " + nano + " (" + player.getName().asString() + ")");
                            return;
                        } else {
                            if (TimeUtil.processMillion(waiting) > loginCheckTimeLimit) {
                                connection.send(new DisconnectS2CPacket(new LiteralText("server enabled ModMdo secure module, please login with ModMdo")));
                                LOGGER.warn("ModMdo reject a login request, player \"" + player.getName().asString() + "\", because player not login with ModMdo");
                                connection.disconnect(new LiteralText("failed to login server"));

                                LOGGER.info("rejected nano: " + nano + " (" + player.getName().asString() + ")");
                                return;
                            }
                        }

                        if (! connection.isOpen()) {
                            break;
                        }

                        try {
                            Thread.sleep(15);
                        } catch (InterruptedException e) {

                        }
                    }
                }

                try {
                    try {
                        if (connection.isOpen()) {
                            serverLogin.login(player.getName().asString(), player.getUuid().toString(), "", "0");

                            server.getPlayerManager().onPlayerConnect(connection, player);
                            LOGGER.info("accepted nano: " + nano + " (" + player.getName().asString() + ")");

                            updateWhitelistNames(server, true);
                            updateTemporaryWhitelistNames(server, true);
                        } else {
                            LOGGER.info("expired nano: " + nano + " (" + player.getName().asString() + ")");
                        }
                    } catch (Exception e) {
                        if (! server.isHost(player.getGameProfile())) {
                            LOGGER.info("player " + player.getName().asString() + " lost status synchronize");

                            player.networkHandler.sendPacket(new DisconnectS2CPacket(new LiteralText("lost status synchronize, please connect again")));

                            player.networkHandler.disconnect(new LiteralText("lost status synchronize"));
                        } else {
                            LOGGER.info("player " + player.getName().asString() + " lost status synchronize, but will not be process");
                        }
                    }
                } catch (Exception e) {

                }
            }).start();
        } else {
            serverLogin.login(player.getName().asString(), player.getUuid().toString(), "", "0");

            this.server.getPlayerManager().onPlayerConnect(this.connection, player);
        }
    }
}
