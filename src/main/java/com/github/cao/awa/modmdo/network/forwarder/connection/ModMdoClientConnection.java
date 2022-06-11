package com.github.cao.awa.modmdo.network.forwarder.connection;

import com.github.cao.awa.modmdo.network.forwarder.connection.setting.*;
import com.github.cao.awa.modmdo.network.forwarder.handler.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import io.netty.bootstrap.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.channel.epoll.*;
import io.netty.channel.socket.nio.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.handshake.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import org.json.*;

import java.net.*;

import static net.minecraft.network.ClientConnection.*;

public class ModMdoClientConnection {
    private final MinecraftServer server;
    private ModMdoConnectionSetting setting;
    private boolean logged = false;
    private String identifier;
    private ClientConnection connection;
    private long maxLoginMillion = 3000;

    public ModMdoClientConnection(MinecraftServer server, InetSocketAddress address, JSONObject loginData) {
        this.server = server;

        connection = EntrustParser.trying(() -> connection = connect(address, false));

        new ModMdoClientDataNetworkHandler(server, address, connection);

        if (connection == null) {
            return;
        }

        send(new HandshakeC2SPacket("0.0.0.0", - 1, NetworkState.PLAY));
        send(new CustomPayloadC2SPacket(SharedVariables.DATA_CHANNEL, new PacketByteBuf(Unpooled.buffer()).writeIdentifier(SharedVariables.LOGIN_CHANNEL).writeString(loginData.toString())));

        setting = ModMdoConnectionSetting.localSettings();
    }

    public static ClientConnection connect(InetSocketAddress address, boolean useEpoll) {
        final ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
        Class<? extends Channel> class_;
        Lazy<? extends EventLoopGroup> lazy;
        if (Epoll.isAvailable() && useEpoll) {
            class_ = EpollSocketChannel.class;
            lazy = EPOLL_CLIENT_IO_GROUP;
        } else {
            class_ = NioSocketChannel.class;
            lazy = CLIENT_IO_GROUP;
        }

        new Bootstrap().group(lazy.get()).handler(new ChannelInitializer<>() {
            protected void initChannel(Channel channel) {
                EntrustExecution.tryTemporary(() -> channel.config().setOption(ChannelOption.TCP_NODELAY, true));
                channel.pipeline().addLast("splitter", new SplitterHandler()).addLast("decoder", new DecoderHandler(NetworkSide.CLIENTBOUND)).addLast("prepender", new SizePrepender()).addLast("encoder", new PacketEncoder(NetworkSide.SERVERBOUND)).addLast("packet_handler", clientConnection);
            }
        }).channel(class_).connect(address.getAddress(), address.getPort()).syncUninterruptibly();
        return clientConnection;
    }

    public void send(Packet<?> packet) {
        connection.send(packet);
    }

    public ModMdoClientConnection(MinecraftServer server, ClientConnection connection) {
        this.server = server;
        this.connection = connection;

        setting = ModMdoConnectionSetting.localSettings();
    }

    public long getMaxLoginMillion() {
        return maxLoginMillion;
    }

    public void setMaxLoginMillion(long maxLoginMillion) {
        this.maxLoginMillion = maxLoginMillion;
    }

    public String getName() {
        return setting.getName();
    }

    public void setName(String name) {
        setting.setName(name);
    }

    public ModMdoConnectionSetting getSetting() {
        return setting;
    }

    public void setSetting(ModMdoConnectionSetting setting) {
        this.setting = setting;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public void disconnect(Text reason) {
        connection.disconnect(reason);
    }
}
