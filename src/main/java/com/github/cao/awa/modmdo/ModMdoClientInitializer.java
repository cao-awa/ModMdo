package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.storage.*;
import net.fabricmc.api.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Client
public class ModMdoClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TRACKER.info("Loading ModMdo " + SharedVariables.VERSION_ID + " (step 2/2)");
        TRACKER.info("Loading private-key for servers");

        SECURE_KEYS.load(staticConfig.getConfigJSONObject("private_key"));
        staticConfig.setIfNoExist("private_verify_key", RandomIdentifier.randomIdentifier(16, true));

        staticConfig.setIfNoExist("secure_level", SECURE_KEYS.getLevel());
    }
}
