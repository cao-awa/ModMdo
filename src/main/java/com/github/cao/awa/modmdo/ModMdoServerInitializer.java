package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.ModMdoType;
import net.fabricmc.api.DedicatedServerModInitializer;

import static com.github.cao.awa.modmdo.storage.SharedVariables.TRACKER;

public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        TRACKER.info("Loading ModMdo " + SharedVariables.VERSION_ID + " (step 2/2)");

        SharedVariables.modMdoType = ModMdoType.SERVER;
    }
}
