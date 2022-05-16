package com.github.zhuaidadaya.modmdo;

import com.github.zhuaidadaya.modmdo.identifier.RandomIdentifier;
import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import net.fabricmc.api.ClientModInitializer;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        modMdoType = ModMdoType.CLIENT;
    }
}
