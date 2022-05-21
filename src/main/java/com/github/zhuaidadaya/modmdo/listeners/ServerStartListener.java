package com.github.zhuaidadaya.modmdo.listeners;

import com.github.zhuaidadaya.modmdo.ModMdoStdInitializer;
import com.github.zhuaidadaya.modmdo.commands.*;
import com.github.zhuaidadaya.modmdo.network.forwarder.process.*;
import com.github.zhuaidadaya.modmdo.storage.Variables;
import com.github.zhuaidadaya.modmdo.subscribable.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ServerStartListener {
    public void listener() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            Variables.server = server;

            try {
                commandRegister = new ModMdoCommandRegister(server);

                ModMdoStdInitializer.initForLevel(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            tps.init(server, -1);
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            try {
                for (ModMdoDataProcessor processor : modmdoConnections) {
                    processor.disconnect();
                }
            } catch (Exception e) {

            }
            tps.stop();
        });
    }
}
