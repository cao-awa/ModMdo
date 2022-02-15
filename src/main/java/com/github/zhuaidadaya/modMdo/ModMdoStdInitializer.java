package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.modMdo.commands.*;
import com.github.zhuaidadaya.modMdo.commands.ranking.RankingCommand;
import com.github.zhuaidadaya.modMdo.commands.jump.JumpCommand;
import com.github.zhuaidadaya.modMdo.format.console.ConsoleTextFormat;
import com.github.zhuaidadaya.modMdo.format.console.LanguageResource;
import com.github.zhuaidadaya.modMdo.lang.Language;
import com.github.zhuaidadaya.modMdo.listeners.ServerStartListener;
import com.github.zhuaidadaya.modMdo.listeners.ServerTickListener;
import com.github.zhuaidadaya.modMdo.login.token.EncryptionTokenUtil;
import com.github.zhuaidadaya.modMdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modMdo.permission.PermissionLevel;
import com.github.zhuaidadaya.modMdo.reads.FileReads;
import com.github.zhuaidadaya.modMdo.resourceLoader.Resources;
import com.github.zhuaidadaya.modMdo.usr.UserUtil;
import com.github.zhuaidadaya.utils.config.EncryptionType;
import com.github.zhuaidadaya.utils.config.ObjectConfigUtil;
import net.fabricmc.api.ModInitializer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

public class ModMdoStdInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        new Thread(() -> {
            Thread.currentThread().setName("ModMdo");

            LOGGER.info("loading ModMdo " + VERSION_ID + " (step 1/2)");
            LOGGER.info("ModMdo Std Initiator running");
            LOGGER.info("loading for ModMdo Std init");

            config = new ObjectConfigUtil(entrust, "config/", "ModMdo.mhf").setNote("""
                    this file is database file of "ModMdo"
                    not configs only
                    so this file maybe get large and large
                    but usually, it will smaller than 1MB
                                    
                    """).setSplitRange(50000).setEncryption(false).setEncryptionHead(false).setEncryptionType(EncryptionType.COMPOSITE_SEQUENCE);

            try {
                initModMdoVariables();
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateModMdoVariables();

            loginUsers = new UserUtil();
            rejectUsers = new UserUtil();

            new HereCommand().register();
            new DimensionHereCommand().register();
            new ModMdoUserCommand().register();
            new ServerTickListener().listener();
            new ServerStartListener().listener();
            new CavaCommand().register();
            new ModMdoConfigCommand().register();
            new TokenCommand().register();
            new BackupCommand().register();
            new AnalyzerCommand().register();
            new ServerCommand().register();
            new RankingCommand().register();
            new JumpCommand().register();

            parseMapFormat();

            LanguageResource resource = new LanguageResource();
            resource.set(Language.CHINESE, "/assets/modmdo/lang/zh_cn.json");
            resource.set(Language.ENGLISH, "/assets/modmdo/lang/en_us.json");
            consoleTextFormat = new ConsoleTextFormat(resource);
        }).start();
    }

    public void initModMdoVariables() {
        if(config.getConfig("default_language") != null)
            language = Language.getLanguageForName(config.getConfigString("default_language"));
        if(config.getConfig("here_command") != null)
            enableHereCommand = config.getConfigString("here_command").equals("enable");
        if(config.getConfig("dead_message") != null)
            enableDeadMessage = config.getConfigString("dead_message").equals("enable");
        if(config.getConfig("cava") != null)
            enableCava = config.getConfigString("cava").equals("enable");
        if(config.getConfig("secure_enchant") != null)
            enableSecureEnchant = config.getConfigString("secure_enchant").equals("enable");
        if(config.getConfig("encryption_token") != null)
            enableEncryptionToken = config.getConfigString("encryption_token").equals("enable");
        if(config.getConfig("check_token_per_tick") != null)
            enableCheckTokenPerTick = config.getConfigString("check_token_per_tick").equals("enable");
        if(config.getConfig("time_active") != null)
            enableSecureEnchant = config.getConfigString("time_active").equals("enable");

        if(config.getConfig("token_by_encryption") != null) {
            LOGGER.info("init token");
            initModMdoToken();
        } else {
            if(enableEncryptionToken) {
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

        if(config.getConfigString("run_command_follow") == null)
            config.set("run_command_follow", PermissionLevel.OPS);
        if(config.getConfigString("join_server_follow") == null)
            config.set("join_server_follow", PermissionLevel.OPS);
    }

    public void parseMapFormat() {
        JSONObject commandMap = new JSONObject(FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource("/assets/modmdo/format/feature_map.json", getClass())))));
        JSONObject versionMap = new JSONObject(FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource("/assets/modmdo/format/versions_map.json", getClass())))));

        for(String s : versionMap.keySet())
            modMdoIdToVersionMap.put(Integer.valueOf(s), versionMap.getString(s));

        for(String s : versionMap.keySet())
            modMdoVersionToIdMap.put(versionMap.getString(s), Integer.valueOf(s));

        for(String s : commandMap.keySet())
            modMdoCommandVersionMap.put(s, commandMap.getInt(s));
    }
}
