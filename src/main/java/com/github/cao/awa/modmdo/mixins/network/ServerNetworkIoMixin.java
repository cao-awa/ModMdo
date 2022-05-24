package com.github.cao.awa.modmdo.mixins.network;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.nio.*;
import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import java.io.*;
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

    @Shadow @Final private static Logger LOGGER;

    /**
     * @author
     */
    @Overwrite
    public void bind(@Nullable InetAddress address, int port) throws IOException {
        synchronized(this.channels) {
            Class class_;
            Lazy lazy;
            Lazy httpLazy;
            if (Epoll.isAvailable() && this.server.isUsingNativeTransport()) {
                class_ = EpollServerSocketChannel.class;
                lazy = EPOLL_CHANNEL;
                LOGGER.info("Using epoll channel type");
            } else {
                class_ = NioServerSocketChannel.class;
                lazy = DEFAULT_CHANNEL;
                LOGGER.info("Using default channel type");
            }

            this.channels.add((new ServerBootstrap()).channel(class_).childHandler(new ChannelInitializer<>() {
                protected void initChannel(Channel channel) {
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
            }).group((EventLoopGroup)lazy.get()).localAddress(address, port).bind().syncUninterruptibly());
        }
    }
}
