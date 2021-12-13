package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.modMdo.type.ModMdoType;
import net.fabricmc.api.ClientModInitializer;

import static com.github.zhuaidadaya.modMdo.storage.Variables.LOGGER;
import static com.github.zhuaidadaya.modMdo.storage.Variables.modMdoType;

public class ModMdoClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LOGGER.info("loading for ModMdo Client");

        modMdoType = ModMdoType.CLIENT;
    }
}
