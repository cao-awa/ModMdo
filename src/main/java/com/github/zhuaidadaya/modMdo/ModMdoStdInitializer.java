package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.MCH.utils.config.ConfigUtil;
import com.github.zhuaidadaya.modMdo.commands.*;
import com.github.zhuaidadaya.modMdo.lang.Language;
import com.github.zhuaidadaya.modMdo.listeners.ServerStartListener;
import com.github.zhuaidadaya.modMdo.listeners.ServerTickListener;
import com.github.zhuaidadaya.modMdo.login.token.EncryptionTokenUtil;
import com.github.zhuaidadaya.modMdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modMdo.usr.UserUtil;
import net.fabricmc.api.ModInitializer;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

public class ModMdoStdInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        LOGGER.info("ModMdo Std Initiator running");
        LOGGER.info("loading for ModMdo Std init");

        config = new ConfigUtil("config/", "ModMdo.mhf", entrust).setNote("""
                this file is database file of "ModMdo"
                not configs only
                so this file maybe get large and large
                but usually, it will smaller than 1MB
                                
                """);

        initModMdoVariables();
        updateModMdoVariables();

        loginUsers = new UserUtil();

        new HereCommand().register();
        new DimensionHereCommand().register();
        new ModMdoUserCommand().register();
        new ServerTickListener().listener();
        new ServerStartListener().listener();
        new CavaCommand().register();
        new ModMdoConfigCommand().register();
        new VecCommand().register();
        new TokenCommand().register();
        new BackupCommand().register();
        new AnalyzerCommand().register();
    }

    public void initModMdoVariables() {
        if(config.getConfig("default_language") != null)
            language = Language.getLanguageForName(config.getConfigValue("default_language"));
        if(config.getConfig("here_command") != null)
            enableHereCommand = config.getConfigValue("here_command").equals("enable");
        if(config.getConfig("dead_message") != null)
            enableDeadMessage = config.getConfigValue("dead_message").equals("enable");
        if(config.getConfig("cava") != null)
            enableCava = config.getConfigValue("cava").equals("enable");
        if(config.getConfig("secure_enchant") != null)
            enableSecureEnchant = config.getConfigValue("secure_enchant").equals("enable");
        if(config.getConfig("encryption_token") != null)
            enableEncryptionToken = config.getConfigValue("encryption_token").equals("enable");

        if(enableEncryptionToken) {
            if(config.getConfig("token_by_encryption") != null) {
                initModMdoToken();
            } else {
                try {
                    modMdoToken = new EncryptionTokenUtil(ServerEncryptionToken.createServerEncryptionToken());
                    LOGGER.info("spawned new encryption token, check the config file");
                } catch (Exception e) {
                    enableEncryptionToken = false;
                    LOGGER.warn("failed to enable encryption token");
                }
            }
        }
    }
}
