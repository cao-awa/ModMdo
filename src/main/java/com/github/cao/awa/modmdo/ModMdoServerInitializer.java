package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.ModMdoType;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        SharedVariables.modMdoType = ModMdoType.SERVER;
    }
}
