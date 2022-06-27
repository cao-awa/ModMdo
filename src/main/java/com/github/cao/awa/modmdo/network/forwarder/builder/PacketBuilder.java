package com.github.cao.awa.modmdo.network.forwarder.builder;

import com.github.cao.awa.modmdo.network.forwarder.connection.setting.*;
import net.minecraft.network.*;

public abstract class PacketBuilder<T extends Packet<?>> {
    public abstract T buildDisconnect(String reason);
    public abstract T buildChat(String message, String player);
    public abstract T buildSetting(ModMdoConnectionSetting setting);
    public abstract T buildLoginSuccess();
    public abstract T buildPlayerJoin(String name);
    public abstract T buildPlayerQuit(String name);
    public abstract T buildKeepAlive(long lastKeepAlive);
}
