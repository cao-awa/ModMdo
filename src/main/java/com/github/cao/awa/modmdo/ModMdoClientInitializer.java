package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.fabricmc.api.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntrustExecution.tryAssertNotNull(System.getProperty("-DmodmdoDebug=true"), debug -> {
            SharedVariables.debug = Boolean.parseBoolean(debug);
            TRACKER.submit("Init modmdo debug as " + debug);
        });
        TRACKER.info("ModMdo Auth loading");

        staticConfig = new DiskObjectConfigUtil("ModMdo", "config/modmdo", "modmdo", false);

        staticConfig.setIfNoExist("identifier", RandomIdentifier.randomIdentifier());

        TRACKER.info("Loading ModMdo Auth" + SharedVariables.VERSION_ID);
        TRACKER.info("Loading private-key for servers");

        SECURE_KEYS.load(staticConfig.getConfigJSONObject("private_key"));
        staticConfig.setIfNoExist("private_verify_key", RandomIdentifier.randomIdentifier(16, true));

        staticConfig.setIfNoExist("secure_level", SECURE_KEYS.getLevel());
    }
}
