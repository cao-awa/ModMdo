package com.github.zhuaidadaya.modmdo;

import com.github.zhuaidadaya.modmdo.event.*;
import com.github.zhuaidadaya.modmdo.extra.loader.*;
import com.github.zhuaidadaya.modmdo.format.console.ConsoleTextFormat;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resource;
import com.github.zhuaidadaya.modmdo.format.minecraft.MinecraftTextFormat;
import com.github.zhuaidadaya.modmdo.identifier.RandomIdentifier;
import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.listeners.ServerStartListener;
import com.github.zhuaidadaya.modmdo.listeners.ServerTickListener;
import com.github.zhuaidadaya.modmdo.permission.PermissionLevel;
import com.github.zhuaidadaya.modmdo.reads.FileReads;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resources;
import com.github.zhuaidadaya.modmdo.type.*;
import com.github.zhuaidadaya.modmdo.utils.usr.UserUtil;
import com.github.zhuaidadaya.modmdo.utils.enchant.EnchantLevelController;
import com.github.zhuaidadaya.rikaishinikui.handler.config.DiskObjectConfigUtil;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoStdInitializer implements ModInitializer {
    public static void initForLevel(MinecraftServer server) {
        event = new ModMdoEventTracer();

        extras.getExtra(ModMdo.class, EXTRA_ID).setServer(server);
        extras.load();
    }

    public static void initModMdoVariables() {
        EntrustExecution.notNull(config.getConfigBoolean("here_command"), here -> {
            enableHereCommand = here;
        });
        EntrustExecution.notNull(config.getConfigBoolean("dead_message"), dMessage-> {
            enableDeadMessage = dMessage;
        });
        EntrustExecution.notNull(config.getConfigBoolean("cava"), cava -> {
            enableCava = cava;
        });
        EntrustExecution.notNull(config.getConfigBoolean("secure_enchant"), enchant -> {
            enableSecureEnchant = enchant;
        });
        EntrustExecution.notNull(config.getConfigBoolean("time_active"), tac -> {
            timeActive = tac;
        });
        EntrustExecution.notNull(config.getConfigBoolean("secure_enchant"), secureEnchant -> {
            enableSecureEnchant = secureEnchant;
        });
        EntrustExecution.notNull(config.getConfigBoolean("enchantment_clear_if_level_too_high"), clear -> {
            clearEnchantIfLevelTooHigh = clear;
        });
        EntrustExecution.notNull(config.getConfigBoolean("reject_no_fall_chest"), rejectnoFall -> {
            rejectNoFallCheat = rejectnoFall;
        });
        EntrustExecution.notNull(config.getConfigBoolean("modmdo_whitelist"), whitelist -> {
            modmdoWhitelist = whitelist;
        });

        EntrustExecution.nullRequires(configCached.getConfig("identifier"), identifier -> {
            configCached.set("identifier", RandomIdentifier.randomIdentifier());
        });

        EntrustExecution.nullRequires(config.getConfigString("run_command_follow"), runCommandFollow -> {
            config.set("run_command_follow", PermissionLevel.OPS);
        });
        EntrustExecution.nullRequires(config.getConfigString("join_server_follow"), joinServerFollow -> {
            config.set("join_server_follow", PermissionLevel.OPS);
        });

        if (modMdoType == ModMdoType.SERVER) {
            initWhiteList();
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Loading ModMdo " + VERSION_ID + " (step 1/2)");
        LOGGER.info("ModMdo Std Initiator running");
        LOGGER.info("Loading for ModMdo Std init");

        configCached = new DiskObjectConfigUtil(entrust, "config/modmdo/");
        configCached.setIfNoExist("identifier", RandomIdentifier.randomIdentifier());

        loginUsers = new UserUtil();
        rejectUsers = new UserUtil();

        parseMapFormat();

        new ServerStartListener().listener();
        new ServerTickListener().listener();

        Resource<Language> resource = new Resource<>();
        resource.set(Language.CHINESE, "/assets/modmdo/lang/zh_cn.json");
        resource.set(Language.ENGLISH, "/assets/modmdo/lang/en_us.json");
        consoleTextFormat = new ConsoleTextFormat(resource);
        minecraftTextFormat = new MinecraftTextFormat(resource);
        Resource<String> enchant = new Resource<>();
        enchant.set("enchantment_level", "/assets/modmdo/format/enchantment_level.json");
        enchantLevelController = new EnchantLevelController(enchant);

        LOGGER.info("Registering for ModMdo major");
        extras = new ModMdoExtraLoader(new ModMdo().setName("ModMdo").setId(EXTRA_ID), RandomIdentifier.randomIdentifier());

        LOGGER.info("Registering for ModMdo extra");
        for (ModMdoExtra<?> extra : extrasWaitingForRegister) {
            extras.register(extra.getId(), extra);
        }

        loaded = true;
    }

    public void parseMapFormat() {
        try {
            JSONObject versionMap = new JSONObject(FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource("/assets/modmdo/versions/versions_map.json", getClass()), StandardCharsets.UTF_8))));

            for (String s : versionMap.keySet())
                modMdoIdToVersionMap.put(Integer.valueOf(s), versionMap.getString(s));

            for (String s : versionMap.keySet())
                modMdoVersionToIdMap.put(versionMap.getString(s), Integer.valueOf(s).intValue());
        } catch (Exception e) {

        }
    }
}
