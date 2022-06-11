package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.identifier.*;
import com.github.cao.awa.modmdo.listeners.*;
import com.github.cao.awa.modmdo.permission.*;
import com.github.cao.awa.modmdo.reads.*;
import com.github.cao.awa.modmdo.resourceLoader.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.type.*;
import com.github.cao.awa.modmdo.utils.enchant.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.fabricmc.api.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import org.json.*;

import java.io.*;
import java.nio.charset.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoStdInitializer implements ModInitializer {
    public static void initForLevel(MinecraftServer server) {
        tracker.submit("ModMdo extra init");
        SharedVariables.extras.getExtra(ModMdo.class, SharedVariables.EXTRA_ID).setServer(server);
        SharedVariables.extras.load();

        loadEvent(false);
    }

    public static Pair<Integer, Integer> loadEvent(boolean reload) {
        tracker.submit(reload ? "ModMdo event reloading" : "ModMdo event loading");
        int old = 0;
        if (event != null) {
            old = event.registered();
        }
        event = new ModMdoEventTracer();
        event.build();
        return new Pair<>(event.registered(), old);
    }

    public static void initModMdoVariables() {
        EntrustExecution.notNull(config.getConfigBoolean("here_command"), here -> {
            SharedVariables.enableHereCommand = here;
        });
        EntrustExecution.notNull(config.getConfigBoolean("cava"), cava -> {
            SharedVariables.enableCava = cava;
        });
        EntrustExecution.notNull(config.getConfigBoolean("secure_enchant"), enchant -> {
            SharedVariables.enableSecureEnchant = enchant;
        });
        EntrustExecution.notNull(config.getConfigBoolean("time_active"), tac -> {
            SharedVariables.timeActive = tac;
        });
        EntrustExecution.notNull(config.getConfigBoolean("secure_enchant"), secureEnchant -> {
            SharedVariables.enableSecureEnchant = secureEnchant;
        });
        EntrustExecution.notNull(config.getConfigBoolean("enchantment_clear_if_level_too_high"), clear -> {
            SharedVariables.clearEnchantIfLevelTooHigh = clear;
        });
        EntrustExecution.notNull(config.getConfigBoolean("reject_no_fall_chest"), rejectnoFall -> {
            SharedVariables.rejectNoFallCheat = rejectnoFall;
        });
        EntrustExecution.notNull(config.getConfigBoolean("modmdo_whitelist"), whitelist -> {
            SharedVariables.modmdoWhitelist = whitelist;
        });

        EntrustExecution.nullRequires(config.getConfigString("run_command_follow"), runCommandFollow -> {
            config.set("run_command_follow", PermissionLevel.OPS);
        });
        EntrustExecution.nullRequires(config.getConfigString("join_server_follow"), joinServerFollow -> {
            config.set("join_server_follow", PermissionLevel.OPS);
        });

        if (SharedVariables.modMdoType == ModMdoType.SERVER) {
            SharedVariables.initWhiteList();
            SharedVariables.initBan();
        }
    }

    @Override
    public void onInitialize() {
        tracker.submit("ModMdo loading");
        SharedVariables.LOGGER.info("Loading ModMdo " + SharedVariables.VERSION_ID + " (step 1/2)");
        SharedVariables.LOGGER.info("ModMdo Std Initiator running");
        SharedVariables.LOGGER.info("Loading for ModMdo Std init");

        staticConfig = new DiskObjectConfigUtil("ModMdo", "config/modmdo", "modmdo", false);

        staticConfig.setIfNoExist("identifier", RandomIdentifier.randomIdentifier());

        event = new ModMdoEventTracer();

        SharedVariables.loginUsers = new UserUtil();
        SharedVariables.rejectUsers = new UserUtil();

        parseMapFormat();

        new ServerStartListener().listener();
        new ServerTickListener().listener();

        Resource<String> enchant = new Resource<>();
        enchant.set("enchantment_level", "/assets/modmdo/format/enchantment_level.json");
        SharedVariables.enchantLevelController = new EnchantLevelController(enchant);

        SharedVariables.LOGGER.info("Registering for ModMdo major");
        SharedVariables.extras = new ModMdoExtraLoader(new ModMdo().setName("ModMdo").setId(SharedVariables.EXTRA_ID), RandomIdentifier.randomIdentifier());

        SharedVariables.LOGGER.info("Registering for ModMdo extra");
        for (ModMdoExtra<?> extra : SharedVariables.extrasWaitingForRegister) {
            SharedVariables.extras.register(extra.getId(), extra);
        }

        SharedVariables.loaded = true;
    }

    public void parseMapFormat() {
        try {
            JSONObject versionMap = new JSONObject(FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource("/assets/modmdo/versions/versions_map.json", getClass()), StandardCharsets.UTF_8))));

            for (String s : versionMap.keySet())
                SharedVariables.modMdoIdToVersionMap.put(Integer.valueOf(s), versionMap.getString(s));

            for (String s : versionMap.keySet())
                SharedVariables.modMdoVersionToIdMap.put(versionMap.getString(s), Integer.valueOf(s).intValue());
        } catch (Exception e) {

        }
    }
}
