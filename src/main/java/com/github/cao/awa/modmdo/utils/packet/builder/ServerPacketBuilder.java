package com.github.cao.awa.modmdo.utils.packet.builder;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.*;

import java.util.function.*;

public class ServerPacketBuilder {
    public static final BiFunction<Identifier, PacketByteBuf, Packet<ClientPlayPacketListener>> CUSTOM = CustomPayloadS2CPacket::new;
    public static final TriFunction<String, byte[], byte[], Packet<ClientLoginPacketListener>> HELLO = LoginHelloS2CPacket::new;
}
