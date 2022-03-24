package com.github.zhuaidadaya.modmdo.jump.server;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;

public class ServerJump {
    public void jump(String host, int port, MinecraftClient client) {
        ConnectScreen.connect(client.currentScreen, client, new ServerAddress(host, port), null);
    }
}
