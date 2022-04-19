package com.github.zhuaidadaya.modmdo;

import com.github.zhuaidadaya.modmdo.commands.*;
import com.github.zhuaidadaya.modmdo.commands.jump.JumpCommand;
import com.github.zhuaidadaya.modmdo.extra.loader.ExtraArgs;
import com.github.zhuaidadaya.modmdo.extra.loader.ModMdo;
import com.github.zhuaidadaya.modmdo.extra.loader.ModMdoExtraLoader;
import com.github.zhuaidadaya.modmdo.format.console.ConsoleTextFormat;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resource;
import com.github.zhuaidadaya.modmdo.format.minecraft.MinecraftTextFormat;
import com.github.zhuaidadaya.modmdo.identifier.RandomIdentifier;
import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.listeners.ServerStartListener;
import com.github.zhuaidadaya.modmdo.listeners.ServerTickListener;
import com.github.zhuaidadaya.modmdo.login.token.EncryptionTokenUtil;
import com.github.zhuaidadaya.modmdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modmdo.permission.PermissionLevel;
import com.github.zhuaidadaya.modmdo.ranking.command.RankingCommand;
import com.github.zhuaidadaya.modmdo.reads.FileReads;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resources;
import com.github.zhuaidadaya.modmdo.utils.usr.UserUtil;
import com.github.zhuaidadaya.modmdo.utils.config.DiskObjectConfigUtil;
import com.github.zhuaidadaya.modmdo.utils.config.ObjectConfigUtil;
import com.github.zhuaidadaya.modmdo.utils.enchant.EnchantLevelController;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoStdInitializer implements ModInitializer {
    public static void initForLevel(MinecraftServer server) {
        extras.setArg(extraId, new ExtraArgs().set("server", server));
        extras.load();
    }

    public static void initModMdoVariables() {
        if (config.getConfig("default_language") != null)
            language = Language.getLanguageForName(config.getConfigString("default_language"));
        if (config.getConfig("here_command") != null)
            enableHereCommand = config.getConfigBoolean("here_command");
        if (config.getConfig("dead_message") != null)
            enableDeadMessage = config.getConfigBoolean("dead_message");
        if (config.getConfig("cava") != null)
            enableCava = config.getConfigBoolean("cava");
        if (config.getConfig("secure_enchant") != null)
            enableSecureEnchant = config.getConfigBoolean("secure_enchant");
        if (config.getConfig("encryption_token") != null)
            enableEncryptionToken = config.getConfigBoolean("encryption_token");
        if (config.getConfig("check_token_per_tick") != null)
            enableCheckTokenPerTick = config.getConfigBoolean("check_token_per_tick");
        if (config.getConfig("time_active") != null)
            enableSecureEnchant = config.getConfigBoolean("time_active");
        if (config.getConfig("checker_time_limit") != null)
            tokenCheckTimeLimit = config.getConfigInt("checker_time_limit");
        if (config.getConfig("identifier") == null)
            config.set("identifier", RandomIdentifier.randomIdentifier());
        if (config.getConfig("enchantment_clear_if_level_too_high") != null)
            clearEnchantIfLevelTooHigh = config.getConfigBoolean("enchantment_clear_if_level_too_high");
        if (config.getConfig("reject_no_fall_cheat") != null)
            rejectNoFallCheat = config.getConfigBoolean("reject_no_fall_chest");

        if (config.getConfig("token_by_encryption") != null) {
            initModMdoToken();
        } else {
            if (enableEncryptionToken) {
                try {
                    modMdoToken = new EncryptionTokenUtil(ServerEncryptionToken.createServerEncryptionToken());
                    LOGGER.info("spawned new encryption token, check the config file");
                } catch (Exception e) {
                    enableEncryptionToken = false;
                    LOGGER.info("failed to enable encryption token");
                }
            } else {
                modMdoToken = new EncryptionTokenUtil();
            }
        }

        if (config.getConfigString("run_command_follow") == null)
            config.set("run_command_follow", PermissionLevel.OPS);
        if (config.getConfigString("join_server_follow") == null)
            config.set("join_server_follow", PermissionLevel.OPS);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("loading ModMdo " + VERSION_ID + " (step 1/2)");
        LOGGER.info("ModMdo Std Initiator running");
        LOGGER.info("loading for ModMdo Std init");

        configCached = new DiskObjectConfigUtil(entrust, "config/modmdo/");

        loginUsers = new UserUtil();
        rejectUsers = new UserUtil();

        parseMapFormat();

        new ServerStartListener().listener();
        new ServerTickListener().listener();

        try {
            new ModMdoUserCommand().register();
            new HereCommand().register();
            new DimensionHereCommand().register();
            new CavaCommand().register();
            new ModMdoConfigCommand().register();
            new TokenCommand().register();
            new AnalyzerCommand().register();
            new RankingCommand().register();
            new JumpCommand().register();
            new TestCommand().register();
        } catch (Exception e) {

        }

        Resource<Language> resource = new Resource<>();
        resource.set(Language.CHINESE, "/assets/modmdo/lang/zh_cn.json");
        resource.set(Language.ENGLISH, "/assets/modmdo/lang/en_us.json");
        consoleTextFormat = new ConsoleTextFormat(resource);
        minecraftTextFormat = new MinecraftTextFormat(resource);
        Resource<String> enchant = new Resource<>();
        enchant.set("enchantment_level", "/assets/modmdo/format/enchantment_level.json");
        enchantLevelController = new EnchantLevelController(enchant);

        extras = new ModMdoExtraLoader();

        LOGGER.info("registering for ModMdo extra");

        extras.register(extraId, new ModMdo().setName("ModMdo"));

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
