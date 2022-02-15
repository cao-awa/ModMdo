package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.modMdo.type.ModMdoType;
import net.fabricmc.api.DedicatedServerModInitializer;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        new Thread(() -> {
            Thread.currentThread().setName("ModMdo");

            LOGGER.info("loading for ModMdo Server (step 2/2)");

            modMdoType = ModMdoType.SERVER;

            if(modMdoToken != null)
                saveToken();
        }).start();
    }
}
