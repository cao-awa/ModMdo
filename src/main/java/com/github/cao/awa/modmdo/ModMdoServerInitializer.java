package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.*;
import net.fabricmc.api.*;
import org.apache.logging.log4j.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Server
public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoInitializer");

    @Override
    public void onInitializeServer() {
        LOGGER.info("Loading ModMdo " + SharedVariables.VERSION_ID + " (step 2/2)");

        staticConfig.setIfNoExist("private_verify_key",
                                  RandomIdentifier.randomIdentifier(
                                          16,
                                          true
                                  )
        );

        SharedVariables.modMdoType = ModMdoType.SERVER;
    }
}
