package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.benchmark.connect.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.authlib.*;
import com.mojang.brigadier.arguments.*;
import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.socket.nio.*;
import io.netty.handler.timeout.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.handshake.*;
import net.minecraft.network.packet.c2s.login.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import static net.minecraft.server.command.CommandManager.*;

public class BenchmarkCommand extends SimpleCommand {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static ClientConnection connect(InetSocketAddress address) {
        final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);

        new Bootstrap().group(ClientConnection.CLIENT_IO_GROUP.get())
                         .handler(new ChannelInitializer<>() {
                             protected void initChannel(Channel channel) {
                                 try {
                                     channel.config()
                                            .setOption(
                                                    ChannelOption.TCP_NODELAY,
                                                    true
                                            );
                                 } catch (ChannelException var3) {

                                 }

                                 channel.pipeline()
                                        .addLast(
                                                "timeout",
                                                new ReadTimeoutHandler(30)
                                        )
                                        .addLast(
                                                "splitter",
                                                new SplitterHandler()
                                        )
                                        .addLast(
                                                "decoder",
                                                new DecoderHandler(NetworkSide.CLIENTBOUND)
                                        )
                                        .addLast(
                                                "prepender",
                                                new SizePrepender()
                                        )
                                        .addLast(
                                                "encoder",
                                                new PacketEncoder(NetworkSide.SERVERBOUND)
                                        )
                                        .addLast(
                                                "packet_handler",
                                                clientConnection
                                        );
                             }
                         })
                         .channel(NioSocketChannel.class)
                         .connect(
                                 address.getAddress(),
                                 address.getPort()
                         )
                         .syncUninterruptibly();
        return clientConnection;
    }

    public BenchmarkCommand register() {
        SharedVariables.commandRegister.register(literal("benchmark").then(literal("connection").then(argument(
                "ip",
                StringArgumentType.string()
        ).then(argument(
                "port",
                IntegerArgumentType.integer()
        ).executes(benchmark -> {
            String ip = StringArgumentType.getString(
                    benchmark,
                    "ip"
            );
            int port = IntegerArgumentType.getInteger(
                    benchmark,
                    "port"
            );
            benchmark(new InetSocketAddress(
                    ip,
                    port
            ));
            return 0;
        })))));
        return this;
    }

    public static void benchmark(InetSocketAddress address) {
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                for (int i1 = 0; i1 < 1000000; i1++) {
                    EntrustEnvironment.trys(() -> connecting(address)
                    );
                }
            }).start();
        }
    }

    public static void connecting(InetSocketAddress address) {
        ClientConnection connection = ClientConnection.connect(
                address,
                 false
        );
        connection.setPacketListener(new ClientLoginNetworkHandlerB(
                connection
        ));
        connection.send(new HandshakeC2SPacket(
                address.getHostName(),
                address.getPort(),
                NetworkState.LOGIN
        ));
        connection.send(new LoginHelloC2SPacket(new GameProfile(
                UUID.randomUUID(),
                "test" + RANDOM.nextInt(1000000)
        )));
    }
}
