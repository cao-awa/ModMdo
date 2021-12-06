package com.github.zhuaidadaya.modMdo.Listeners;

import com.github.zhuaidadaya.modMdo.Storage.Variables;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.*;

public class ServerStartListener {
    public void listener() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            motd = server.getServerMotd();
            Variables.server = server;
        });
    }
}
