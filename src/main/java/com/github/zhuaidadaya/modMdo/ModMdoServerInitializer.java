package com.github.zhuaidadaya.modMdo;

import net.fabricmc.api.DedicatedServerModInitializer;

import static com.github.zhuaidadaya.modMdo.storage.Variables.LOGGER;

public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        LOGGER.info("loading for ModMdo Server");
    }
}
