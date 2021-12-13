package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.modMdo.type.ModMdoType;
import net.fabricmc.api.DedicatedServerModInitializer;

import static com.github.zhuaidadaya.modMdo.storage.Variables.LOGGER;
import static com.github.zhuaidadaya.modMdo.storage.Variables.modMdoType;

public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        LOGGER.info("loading for ModMdo Server");

        modMdoType = ModMdoType.SERVER;
    }
}
