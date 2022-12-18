package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.config.*;
import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.storage.*;
import net.fabricmc.api.*;
import org.apache.logging.log4j.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoClientInitializer implements ClientModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoInitializer");

    @Override
    public void onInitializeClient() {
        LOGGER.info(
                "Loading ModMdo Auth '{}'",
                SharedVariables.VERSION_ID
        );

        staticConfig = new DiskConfigUtil(
                "ModMdo",
                "config/modmdo",
                "modmdo",
                false
        );

        staticConfig.setIfNoExist(
                "identifier",
                RandomIdentifier.randomIdentifier(
                        4096,
                        true
                )
        );

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
