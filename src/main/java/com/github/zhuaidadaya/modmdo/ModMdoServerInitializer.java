package com.github.zhuaidadaya.modmdo;

import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import net.fabricmc.api.DedicatedServerModInitializer;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        modMdoType = ModMdoType.SERVER;
    }
}
