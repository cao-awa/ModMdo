package com.github.zhuaidadaya.modmdo;

import com.github.zhuaidadaya.modmdo.identifier.RandomIdentifier;
import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import net.fabricmc.api.ClientModInitializer;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Thread thread = new Thread(() -> {
            Thread.currentThread().setName("ModMdo");

            LOGGER.info("loading for ModMdo Client (step 2/2)");

            modMdoType = ModMdoType.CLIENT;

            config.setIfNoExist("identifier", RandomIdentifier.randomIdentifier());
        });

        thread.setName("ModMdo Client");

        thread.start();
    }
}
