package com.github.zhuaidadaya.modmdo;

import com.github.zhuaidadaya.modmdo.type.ModMdoType;
import net.fabricmc.api.DedicatedServerModInitializer;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        modMdoType = ModMdoType.SERVER;

        if (modMdoToken != null)
            saveToken();
    }
}
