package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.ModMdoType;
import net.fabricmc.api.ClientModInitializer;

public class ModMdoClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SharedVariables.modMdoType = ModMdoType.CLIENT;
    }
}
