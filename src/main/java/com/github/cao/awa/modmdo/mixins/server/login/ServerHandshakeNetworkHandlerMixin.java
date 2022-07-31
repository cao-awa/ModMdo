package com.github.cao.awa.modmdo.mixins.server.login;

import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.spongepowered.asm.mixin.*;

@Mixin(ServerHandshakeNetworkHandler.class)
public class ServerHandshakeNetworkHandlerMixin {
    @Shadow @Final private ClientConnection connection;

    @Shadow @Final private MinecraftServer server;

    @Shadow @Final private static Text IGNORING_STATUS_REQUEST_MESSAGE;
}
