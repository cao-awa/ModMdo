package com.github.zhuaidadaya.modmdo.network.builder;

import com.github.zhuaidadaya.modmdo.network.connection.setting.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;

public abstract class PacketBuilder<T extends Packet<?>> {
    public abstract T buildDisconnect(String reason);
    public abstract T buildChat(String message, String player);
    public abstract T buildSetting(ModMdoConnectionSetting setting);
    public abstract T buildLoginSuccess();
    public abstract T buildPlayerJoin(String name);
    public abstract T buildPlayerQuit(String name);
    public abstract T buildKeepAlive(long lastKeepAlive);
    public abstract T buildTraffic(OperationalLong traffic, Object2ObjectRBTreeMap<String, OperationalLong> processed);
    public abstract T buildTrafficResult(OperationalLong traffic, Object2ObjectRBTreeMap<String, OperationalLong> processed);
}
