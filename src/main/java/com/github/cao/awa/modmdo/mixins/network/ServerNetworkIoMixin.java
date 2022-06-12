package com.github.cao.awa.modmdo.mixins.network;

import com.github.cao.awa.modmdo.storage.*;
import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.nio.*;
import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import java.net.*;
import java.util.*;

@Mixin(ServerNetworkIo.class)
public class ServerNetworkIoMixin {
    @Shadow @Final private List<ChannelFuture> channels;

    @Shadow @Final public static Lazy<EpollEventLoopGroup> EPOLL_CHANNEL;

    @Shadow @Final public static Lazy<NioEventLoopGroup> DEFAULT_CHANNEL;

    @Shadow @Final
    MinecraftServer server;

    @Shadow @Final
    List<ClientConnection> connections;

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public void bind(@Nullable InetAddress address, int port) {
        synchronized(this.channels) {
            Class<? extends ServerChannel> clazz;
            Lazy<? extends EventLoopGroup> lazy;
            if (Epoll.isAvailable() && this.server.isUsingNativeTransport()) {
                clazz = EpollServerSocketChannel.class;
                lazy = EPOLL_CHANNEL;
                SharedVariables.LOGGER.info("Using epoll channel type");
            } else {
                clazz = NioServerSocketChannel.class;
                lazy = DEFAULT_CHANNEL;
                SharedVariables.LOGGER.info("Using default channel type");
            }

            this.channels.add((new ServerBootstrap()).channel(clazz).childHandler(new ChannelInitializer<>() {
                protected void initChannel(@NotNull Channel channel) {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException var4) {

                    }

                    channel.pipeline().addLast("splitter", new SplitterHandler()).addLast("decoder", new DecoderHandler(NetworkSide.SERVERBOUND)).addLast("prepender", new SizePrepender()).addLast("encoder", new PacketEncoder(NetworkSide.CLIENTBOUND));

                    int i = server.getRateLimit();
                    ClientConnection clientConnection = i > 0 ? new RateLimitedConnection(i) : new ClientConnection(NetworkSide.SERVERBOUND);
                    connections.add(clientConnection);
                    channel.pipeline().addLast("packet_handler", clientConnection);
                    clientConnection.setPacketListener(new ServerHandshakeNetworkHandler(server, clientConnection));
                }
            }).group(lazy.get()).localAddress(address, port).bind().syncUninterruptibly());
        }
    }
}
