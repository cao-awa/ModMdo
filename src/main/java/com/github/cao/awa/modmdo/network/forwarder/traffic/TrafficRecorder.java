package com.github.cao.awa.modmdo.network.forwarder.traffic;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;

public class TrafficRecorder {
    private final Object2ObjectRBTreeMap<String, OperationalLong> packetsOut = new Object2ObjectRBTreeMap<>();
    private final Object2ObjectRBTreeMap<String, OperationalLong> packetsIn = new Object2ObjectRBTreeMap<>();
    private final OperationalLong trafficOut = new OperationalLong();
    private final OperationalLong trafficIn = new OperationalLong();

    public void process(CustomPayloadC2SPacket packet) {
        PacketByteBuf buf = new PacketByteBuf(packet.getData().copy());
        buf.readIdentifier();
        buf.readString();
    }

    public void process(CustomPayloadS2CPacket packet) {

    }
}
