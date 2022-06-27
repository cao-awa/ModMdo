package com.github.cao.awa.modmdo.mixins.connection;

import net.minecraft.network.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(ClientConnection.class)
public interface ClientConnectionInterface {
    @Accessor
    int getPacketsSentCounter();

    @Accessor
    int getPacketsReceivedCounter();
}
