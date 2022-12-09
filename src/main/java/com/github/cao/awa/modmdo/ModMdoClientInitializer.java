package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.*;
import net.fabricmc.api.*;
import org.apache.logging.log4j.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Client
public class ModMdoClientInitializer implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoInitializer");

    @Override
    public void onInitializeClient() {
        LOGGER.info(
                "Loading ModMdo {} (step 2/2)",
                SharedVariables.VERSION_ID
        );
        SharedVariables.modMdoType = ModMdoType.CLIENT;
        LOGGER.info("Loading private-key for servers");

        SECURE_KEYS.load(staticConfig.getJSONObject("private_key"));
        staticConfig.setIfNoExist(
                "private_verify_key",
                RandomIdentifier.randomIdentifier(
                        16,
                        true
                )
        );

        staticConfig.setIfNoExist(
                "secure_level",
                SECURE_KEYS.getLevel()
        );
    }
}
