package com.github.cao.awa.modmdo;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.format.console.*;
import com.github.cao.awa.modmdo.format.minecraft.*;
import com.github.cao.awa.modmdo.identifier.*;
import com.github.cao.awa.modmdo.lang.*;
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
import org.json.*;

import java.io.*;
import java.nio.charset.*;

public class ModMdoStdInitializer implements ModInitializer {
    public static void initForLevel(MinecraftServer server) {
        SharedVariables.event = new ModMdoEventTracer();

        SharedVariables.extras.getExtra(ModMdo.class, SharedVariables.EXTRA_ID).setServer(server);
        SharedVariables.extras.load();
    }

    public static void initModMdoVariables() {
        EntrustExecution.notNull(SharedVariables.config.getConfigBoolean("here_command"), here -> {
            SharedVariables.enableHereCommand = here;
        });
        EntrustExecution.notNull(SharedVariables.config.getConfigBoolean("dead_message"), dMessage-> {
            SharedVariables.enableDeadMessage = dMessage;
        });
        EntrustExecution.notNull(SharedVariables.config.getConfigBoolean("cava"), cava -> {
            SharedVariables.enableCava = cava;
        });
        EntrustExecution.notNull(SharedVariables.config.getConfigBoolean("secure_enchant"), enchant -> {
            SharedVariables.enableSecureEnchant = enchant;
        });
        EntrustExecution.notNull(SharedVariables.config.getConfigBoolean("time_active"), tac -> {
            SharedVariables.timeActive = tac;
        });
        EntrustExecution.notNull(SharedVariables.config.getConfigBoolean("secure_enchant"), secureEnchant -> {
            SharedVariables.enableSecureEnchant = secureEnchant;
        });
        EntrustExecution.notNull(SharedVariables.config.getConfigBoolean("enchantment_clear_if_level_too_high"), clear -> {
            SharedVariables.clearEnchantIfLevelTooHigh = clear;
        });
        EntrustExecution.notNull(SharedVariables.config.getConfigBoolean("reject_no_fall_chest"), rejectnoFall -> {
            SharedVariables.rejectNoFallCheat = rejectnoFall;
        });
        EntrustExecution.notNull(SharedVariables.config.getConfigBoolean("modmdo_whitelist"), whitelist -> {
            SharedVariables.modmdoWhitelist = whitelist;
        });

        EntrustExecution.nullRequires(SharedVariables.configCached.getConfig("identifier"), identifier -> {
            SharedVariables.configCached.set("identifier", RandomIdentifier.randomIdentifier());
        });

        EntrustExecution.nullRequires(SharedVariables.config.getConfigString("run_command_follow"), runCommandFollow -> {
            SharedVariables.config.set("run_command_follow", PermissionLevel.OPS);
        });
        EntrustExecution.nullRequires(SharedVariables.config.getConfigString("join_server_follow"), joinServerFollow -> {
            SharedVariables.config.set("join_server_follow", PermissionLevel.OPS);
        });

        if (SharedVariables.modMdoType == ModMdoType.SERVER) {
            SharedVariables.initWhiteList();
        }
    }

    @Override
    public void onInitialize() {
        SharedVariables.LOGGER.info("Loading ModMdo " + SharedVariables.VERSION_ID + " (step 1/2)");
        SharedVariables.LOGGER.info("ModMdo Std Initiator running");
        SharedVariables.LOGGER.info("Loading for ModMdo Std init");

        SharedVariables.configCached = new DiskObjectConfigUtil(SharedVariables.entrust, "config/modmdo/");
        SharedVariables.configCached.setIfNoExist("identifier", RandomIdentifier.randomIdentifier());

        SharedVariables.loginUsers = new UserUtil();
        SharedVariables.rejectUsers = new UserUtil();

        parseMapFormat();

        new ServerStartListener().listener();
        new ServerTickListener().listener();

        Resource<Language> resource = new Resource<>();
        resource.set(Language.CHINESE, "/assets/modmdo/lang/zh_cn.json");
        resource.set(Language.ENGLISH, "/assets/modmdo/lang/en_us.json");

        EntrustExecution.tryTemporary(() -> {
            new File("config/modmdo/resources/lang/").mkdirs();

            for (File f : EntrustParser.getNotNull(new File("config/modmdo/resources/lang/").listFiles(), new File[0])) {
                resource.set(Language.ofs(f.getName()), f.getAbsolutePath());
            }
        });
        SharedVariables.consoleTextFormat = new ConsoleTextFormat(resource);
        SharedVariables.minecraftTextFormat = new MinecraftTextFormat(resource);
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
