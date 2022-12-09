package com.github.cao.awa.modmdo.utils.packet.sender;

import com.github.cao.awa.modmdo.utils.packet.buf.*;
import com.github.cao.awa.modmdo.utils.packet.builder.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

public class ServerPacketSender extends PacketSender<ServerPacketSender> {
    public ServerPacketSender(@NotNull ClientConnection connection, @NotNull Identifier channel) {
        super(connection, channel);
    }

    public ServerPacketSender chanel(@NotNull Identifier channel) {
        this.setChannel(channel);
        return this;
    }

    public ServerPacketSender swap(@NotNull ClientConnection connection) {
        send();
        this.setSwapConnection(this.getConnection());
        this.setConnection(connection);
        return this;
    }

    public PacketByteBufBuilder custom() {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        this.prepare(ServerPacketBuilder.CUSTOM.apply(this.getChannel(), buf));
        return new PacketByteBufBuilder(this, buf);
    }

    public ServerPacketSender hello(String serverId, byte[] publicKey, byte[] nonce) {
        this.prepare(ServerPacketBuilder.HELLO.apply(serverId, publicKey, nonce));
        return this;
    }
}
