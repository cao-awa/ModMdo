package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.modMdo.Commands.ArgumentInit;
import net.fabricmc.api.ClientModInitializer;

public class ModMdoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new ArgumentInit().init();
    }
}
