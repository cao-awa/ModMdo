package com.github.zhuaidadaya.modmdo;

import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import net.fabricmc.api.DedicatedServerModInitializer;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Thread thread = new Thread(() -> {
            Thread.currentThread().setName("ModMdo");

            LOGGER.info("loading for ModMdo Server (step 2/2)");

            modMdoType = ModMdoType.SERVER;

            if(modMdoToken != null)
                saveToken();
        });

        thread.setName("ModMdo Server");

        thread.start();
    }
}
