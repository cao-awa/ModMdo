package com.github.cao.awa.modmdo.utils.packet.sender;

import com.github.cao.awa.modmdo.utils.packet.buf.*;
import com.github.cao.awa.modmdo.utils.packet.builder.*;
import com.mojang.authlib.*;
import io.netty.buffer.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;

public class ClientPacketSender extends PacketSender<ClientPacketSender> {
    public ClientPacketSender(@NotNull ClientConnection connection, @NotNull Identifier channel) {
        super(connection, channel);
    }

    public ClientPacketSender chanel(@NotNull Identifier channel) {
        this.setChannel(channel);
        return this;
    }

    public ClientPacketSender swap(@NotNull ClientConnection connection) {
        send();
        this.setSwapConnection(this.getConnection());
        this.setConnection(connection);
        return this;
    }

    public PacketByteBufBuilder custom() {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        this.prepare(ClientPacketBuilder.CUSTOM.apply(this.getChannel(), buf));
        return new PacketByteBufBuilder(this, buf);
    }

    public ClientPacketSender hello(GameProfile profile) {
        this.prepare(ClientPacketBuilder.HELLO.apply(profile));
        return this;
    }
}
