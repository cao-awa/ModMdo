package com.github.zhuaidadaya.modmdo;

import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import net.fabricmc.api.ClientModInitializer;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

public class ModMdoClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        modMdoType = ModMdoType.CLIENT;
    }
}
