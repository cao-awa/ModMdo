package com.github.zhuaidadaya.modmdo.listeners;

import com.github.zhuaidadaya.modmdo.ModMdoStdInitializer;
import com.github.zhuaidadaya.modmdo.storage.Variables;
import com.github.zhuaidadaya.modmdo.utils.file.FileUtil;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import java.io.File;
import java.io.FileOutputStream;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ServerStartListener {
    public void listener() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Variables.server = server;
            try {
                ModMdoStdInitializer.initForLevel(server);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
