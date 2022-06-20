package com.github.cao.awa.modmdo.listeners;

import com.github.cao.awa.modmdo.event.server.tick.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ServerTickListener {
    public void listener() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {

        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            event.submit(new GameTickStartEvent(server));
        });
    }
}
