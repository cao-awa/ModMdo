package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.commands.*;
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
import com.github.cao.awa.modmdo.utils.file.reads.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.*;
import net.minecraft.server.*;
import org.json.*;

import java.io.*;
import java.nio.charset.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoStdInitializer implements ModInitializer {
    public static void initModMdoVariables(ModMdoType type) {
        if (type == ModMdoType.SERVER) {
            TRACKER.submit("Init level as server mode");
        } else {
            TRACKER.submit("Init level as client mode");
        }
        EntrustEnvironment.notNull(
                config.getConfigBoolean("here_command"),
                here -> {
                    SharedVariables.enableHereCommand = here;
                    TRACKER.submit("Init config here_command as " + here);
                }
        );
        EntrustEnvironment.notNull(
                config.getConfigBoolean("secure_enchant"),
                enchant -> {
                    SharedVariables.enableSecureEnchant = enchant;
                    TRACKER.submit("Init config secure_enchant as " + enchant);
                }
        );
        EntrustEnvironment.notNull(
                config.getConfigBoolean("time_active"),
                tac -> {
                    SharedVariables.timeActive = tac;
                    TRACKER.submit("Init config time_active as " + tac);
                }
        );
        EntrustEnvironment.notNull(
                config.getConfigBoolean("modmdo_whitelist"),
                whitelist -> {
                    SharedVariables.modmdoWhitelist = whitelist;
                    TRACKER.submit("Init config modmdo_whitelist as " + whitelist);
                }
        );


        if (type == ModMdoType.CLIENT) {
            EntrustEnvironment.notNull(
                    config.get("secure_level"),
                    level -> {
                        SECURE_KEYS.setLevel(SecureLevel.of(level));
                        TRACKER.submit("Init config secure_level as " + level);
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
                    TRACKER.submit("Init modmdo debug as " + debug);
                }
        );
        TRACKER.submit("ModMdo loading");
        TRACKER.info("Loading ModMdo " + SharedVariables.VERSION_ID + " (step 1/2)");
        TRACKER.info("ModMdo Std Initiator running");
        TRACKER.info("Loading for ModMdo Std init");

        staticConfig = new DiskObjectConfigUtil(
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

            EntrustExecution.tryTemporary(() -> {
                commandRegister = new ModMdoCommandRegister(server);

                ModMdoStdInitializer.initForLevel(server);
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

        TRACKER.info("Registering for ModMdo major");
        SharedVariables.extras = new ModMdoExtraLoader(new ModMdo().setName("ModMdo")
                                                                   .setId(SharedVariables.EXTRA_ID));

        SharedVariables.loaded = true;
    }

    public static void initForLevel(MinecraftServer server) {
        TRACKER.submit("ModMdo extra init");
        SharedVariables.extras.getExtra(
                               ModMdo.class,
                               SharedVariables.EXTRA_ID
                       )
                              .setServer(server);
        SharedVariables.extras.load();

        loadEvent(false);
    }

    public static Legacy<Integer, Integer> loadEvent(boolean reload) {
        TRACKER.submit(reload ? "ModMdo event reloading" : "ModMdo event loading");
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
            JSONObject versionMap = new JSONObject(FileReads.read(new BufferedReader(new InputStreamReader(
                    Resources.getResource(
                            "assets/modmdo/versions/versions_map.json",
                            getClass()
                    ),
                    StandardCharsets.UTF_8
            ))));

            for (String s : versionMap.keySet())
                SharedVariables.modMdoIdToVersionMap.put(
                        Integer.valueOf(s),
                        versionMap.getString(s)
                );

            for (String s : versionMap.keySet())
                SharedVariables.modMdoVersionToIdMap.put(
                        versionMap.getString(s),
                        Integer.valueOf(s)
                               .intValue()
                );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
