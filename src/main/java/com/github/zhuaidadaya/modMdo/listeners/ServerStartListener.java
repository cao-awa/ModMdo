package com.github.zhuaidadaya.modMdo.listeners;

import com.github.zhuaidadaya.modMdo.storage.Variables;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import static com.github.zhuaidadaya.modMdo.storage.Variables.motd;

public class ServerStartListener {
    public void listener() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            motd = server.getServerMotd();
            Variables.server = server;
        });
    }
}
