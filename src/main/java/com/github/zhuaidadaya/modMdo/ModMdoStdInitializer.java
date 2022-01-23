package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.modMdo.commands.*;
import com.github.zhuaidadaya.modMdo.commands.ranking.RankingCommand;
import com.github.zhuaidadaya.modMdo.lang.Language;
import com.github.zhuaidadaya.modMdo.listeners.ServerStartListener;
import com.github.zhuaidadaya.modMdo.listeners.ServerTickListener;
import com.github.zhuaidadaya.modMdo.login.token.EncryptionTokenUtil;
import com.github.zhuaidadaya.modMdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modMdo.usr.UserUtil;
import com.github.zhuaidadaya.utils.config.EncryptionType;
import com.github.zhuaidadaya.utils.config.ObjectConfigUtil;
import net.fabricmc.api.ModInitializer;

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
    }
}
