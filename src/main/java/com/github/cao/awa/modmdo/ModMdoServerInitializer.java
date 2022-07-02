package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.ModMdoType;
import net.fabricmc.api.DedicatedServerModInitializer;

import static com.github.cao.awa.modmdo.storage.SharedVariables.TRACKER;
import static com.github.cao.awa.modmdo.storage.SharedVariables.staticConfig;

@Server
public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        TRACKER.info("Loading ModMdo " + SharedVariables.VERSION_ID + " (step 2/2)");

        staticConfig.setIfNoExist("private_verify_key", RandomIdentifier.randomIdentifier(16, true));

        SharedVariables.modMdoType = ModMdoType.SERVER;
    }
}
