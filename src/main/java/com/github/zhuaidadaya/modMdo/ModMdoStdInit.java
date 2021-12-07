package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.MCH.Utils.Config.ConfigUtil;
import com.github.zhuaidadaya.modMdo.commands.*;
import com.github.zhuaidadaya.modMdo.lang.Language;
import com.github.zhuaidadaya.modMdo.lang.LanguageDictionary;
import com.github.zhuaidadaya.modMdo.listeners.ServerStartListener;
import com.github.zhuaidadaya.modMdo.listeners.ServerTickListener;
import net.fabricmc.api.ModInitializer;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

public class ModMdoStdInit implements ModInitializer {

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

        languageDictionary = new LanguageDictionary("/format/format.json");

        new HereCommand().register();
        new DimensionHereCommand().register();
        new ModMdoUserCommand().register();
        new ServerTickListener().listener();
        new ServerStartListener().listener();
        new CavaCommand().register();
        new ReloadCommand().register();
    }

    public void initModMdoVariables() {
        if(config.getConfig("default_language") != null)
            language = Language.getLanguageForName(config.getConfig("default_language").getValue());
        if(config.getConfig("here_command") != null)
            enableHereCommand = config.getConfig("here_command").getValue().equals("enable");
        if(config.getConfig("dead_message") != null)
            enableDeadMessage = config.getConfig("dead_message").getValue().equals("enable");
    }
}
