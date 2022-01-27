package com.github.zhuaidadaya.modMdo.wrap.server;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;

public class ServerWrap {
    public void wrap(String host, int port, MinecraftClient client) {
        ConnectScreen.connect(client.currentScreen, client, new ServerAddress(host, port), null);
    }
}
