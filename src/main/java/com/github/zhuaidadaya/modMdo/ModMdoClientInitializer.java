package com.github.zhuaidadaya.modMdo;

import net.fabricmc.api.ClientModInitializer;

import static com.github.zhuaidadaya.modMdo.storage.Variables.LOGGER;

public class ModMdoClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LOGGER.info("loading for ModMdo Client");
    }
}
