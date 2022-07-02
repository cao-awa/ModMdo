package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.fabricmc.api.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoStdInitializer implements ModInitializer {
    @Override
    public void onInitialize() {
        EntrustExecution.tryAssertNotNull(System.getProperty("-DmodmdoDebug=true"), debug -> {
            SharedVariables.debug = Boolean.parseBoolean(debug);
            TRACKER.submit("Init modmdo debug as " + debug);
        });
        TRACKER.submit("ModMdo auth loading");
        TRACKER.info("Loading ModMdo auth " + SharedVariables.VERSION_ID + " (step 1/2)");
        TRACKER.info("ModMdo Std Initiator running");
        TRACKER.info("Loading for ModMdo Std init");

        staticConfig = new DiskObjectConfigUtil("ModMdo", "config/modmdo", "modmdo", false);

        staticConfig.setIfNoExist("identifier", RandomIdentifier.randomIdentifier());
    }
}
