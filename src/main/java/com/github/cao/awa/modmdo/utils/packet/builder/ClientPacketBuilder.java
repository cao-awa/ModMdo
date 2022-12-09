package com.github.cao.awa.modmdo.utils.packet.builder;

import com.mojang.authlib.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.*;

import java.util.function.*;

public class ClientPacketBuilder {
    public static final BiFunction<Identifier, PacketByteBuf, Packet<ServerPlayPacketListener>> CUSTOM = CustomPayloadC2SPacket::new;
    public static final Function<GameProfile, Packet<ServerLoginPacketListener>> HELLO = LoginHelloC2SPacket::new;
}
