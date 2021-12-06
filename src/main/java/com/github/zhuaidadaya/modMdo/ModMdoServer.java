package com.github.zhuaidadaya.modMdo;

import net.fabricmc.api.DedicatedServerModInitializer;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.LOGGER;

public class ModMdoServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        LOGGER.info("loading for ModMdo Server");

        new ModMdoStdInit().init();
    }
}
