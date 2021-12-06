package com.github.zhuaidadaya.modMdo;

import net.fabricmc.api.ClientModInitializer;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.LOGGER;

public class ModMdoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LOGGER.info("loading for ModMdo Server");

        new ModMdoStdInit().init();
    }
}
