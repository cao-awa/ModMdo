package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.config.*;
import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.server.*;
import com.github.cao.awa.modmdo.event.server.tick.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.resourceLoader.*;
import com.github.cao.awa.modmdo.security.*;
import com.github.cao.awa.modmdo.security.level.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.usr.*;
import com.github.cao.awa.modmdo.utils.io.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.server.*;
import org.apache.logging.log4j.*;
import org.json.*;

import java.io.*;
import java.nio.charset.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoStdInitializer implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoInitializer");
    
    public static void initModMdoVariables(ModMdoType type) {
        if (type == ModMdoType.SERVER) {
            LOGGER.debug("Init level as server mode");
        } else {
            LOGGER.debug("Init level as client mode");
        }
        EntrustEnvironment.notNull(
                config.getBoolean("here_command"),
                here -> {
                    SharedVariables.enableHereCommand = here;
                    LOGGER.debug("Init config here_command as " + here);
                }
        );
        EntrustEnvironment.notNull(
                config.getBoolean("secure_enchant"),
                enchant -> {
                    SharedVariables.enableSecureEnchant = enchant;
                    LOGGER.debug("Init config secure_enchant as " + enchant);
                }
        );
        EntrustEnvironment.notNull(
                config.getBoolean("time_active"),
                tac -> {
                    SharedVariables.timeActive = tac;
                    LOGGER.debug("Init config time_active as " + tac);
                }
        );
        EntrustEnvironment.notNull(
                config.getBoolean("modmdo_whitelist"),
                whitelist -> {
                    SharedVariables.modmdoWhitelist = whitelist;
                    LOGGER.debug("Init config modmdo_whitelist as " + whitelist);
                }
        );


        if (type == ModMdoType.CLIENT) {
            EntrustEnvironment.notNull(
                    config.getString("secure_level"),
                    level -> {
                        SECURE_KEYS.setLevel(SecureLevel.of(level));
                        LOGGER.debug("Init config secure_level as " + level);
                    }
            );
        }

        if (type == ModMdoType.SERVER) {
            SharedVariables.initWhiteList();
            SharedVariables.initBan();
        }

        SharedVariables.initNotes();
    }

    @Override
    public void onInitialize() {
        EntrustEnvironment.notNull(
                System.getProperty("-DmodmdoDebug"),
                debug -> {
                    SharedVariables.debug = Boolean.parseBoolean(debug);
                    LOGGER.debug("Init modmdo debug as " + debug);
                }
        );
        LOGGER.debug("ModMdo loading");
        LOGGER.info("Loading ModMdo " + SharedVariables.VERSION_ID + " (step 1/2)");
        LOGGER.info("ModMdo Std Initiator running");
        LOGGER.info("Loading for ModMdo Std init");

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

        SharedVariables.loginUsers = new Users();
        SharedVariables.rejectUsers = new Users();

        parseMapFormat();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            SharedVariables.server = server;

            EntrustEnvironment.trys(() -> {
                commandRegister = new ModMdoCommandRegister(server);

                ModMdoStdInitializer.initForLevel(server);

                new BenchmarkCommand().register();
            });
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            if (event != null) {
                event.submit(new ServerStartedEvent(server));
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            event.submit(new GameTickEndEvent(server));
        });

        ServerTickEvents.START_SERVER_TICK.register(server -> {
            event.submit(new GameTickStartEvent(server));
        });

        Resource<String> enchant = new Resource<>();
        enchant.set(
                "enchantment_level",
                "assets/modmdo/format/enchantment_level.json"
        );

        LOGGER.info("Registering for ModMdo major");
        SharedVariables.extras = new ModMdoExtraLoader(new ModMdo().setName("ModMdo")
                                                                   .setId(SharedVariables.EXTRA_ID));

        SharedVariables.loaded = true;
    }

    public static void initForLevel(MinecraftServer server) {
        LOGGER.debug("ModMdo extra init");
        SharedVariables.extras.getExtra(
                               ModMdo.class,
                               SharedVariables.EXTRA_ID
                       )
                              .setServer(server);
        SharedVariables.extras.load();

        loadEvent(false);
    }

    public static Legacy<Integer, Integer> loadEvent(boolean reload) {
        LOGGER.debug(reload ? "ModMdo event reloading" : "ModMdo event loading");
        int old = 0;
        if (event != null) {
            old = event.registered();
        }
        event = new ModMdoEventTracer();
        event.build();
        return new Legacy<>(
                event.registered(),
                old
        );
    }

    public void parseMapFormat() {
        try {
            JSONObject versionMap = new JSONObject(IOUtil.read(new BufferedReader(new InputStreamReader(
                    Resources.getResource(
                            "assets/modmdo/versions/versions_map.json",
                            getClass()
                    ),
                    StandardCharsets.UTF_8
            ))));

            for (String s : versionMap.keySet())
                SharedVariables.MOD_MDO_ID_TO_VERSION_MAP.put(
                        Integer.valueOf(s),
                        versionMap.getString(s)
                );

            for (String s : versionMap.keySet())
                SharedVariables.MOD_MDO_VERSION_TO_ID_MAP.put(
                        versionMap.getString(s),
                        Integer.valueOf(s)
                               .intValue()
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
