package com.github.cao.awa.modmdo.attack;

import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;

public class UnderAttackHandler implements ServerLoginPacketListener {
    private final @NotNull ClientConnection connection;

    public UnderAttackHandler(@NotNull ClientConnection connection) {
        this.connection = connection;
    }

    @Override
    public void onHello(LoginHelloC2SPacket packet) {

    }

    @Override
    public void onKey(LoginKeyC2SPacket packet) {

    }

    @Override
    public void onQueryResponse(LoginQueryResponseC2SPacket packet) {

    }

    @Override
    public void onDisconnected(Text reason) {

    }

    @Override
    public @NotNull ClientConnection getConnection() {
        return connection;
    }
}
