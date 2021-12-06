package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.MCH.Utils.Config.ConfigUtil;
import com.github.zhuaidadaya.modMdo.Commands.DimensionHereCommand;
import com.github.zhuaidadaya.modMdo.Commands.HereCommand;
import com.github.zhuaidadaya.modMdo.Commands.ModMdoUserCommand;
import com.github.zhuaidadaya.modMdo.Lang.LanguageDictionary;
import com.github.zhuaidadaya.modMdo.Listeners.ServerStartListener;
import com.github.zhuaidadaya.modMdo.Listeners.ServerTickListener;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.*;

public class ModMdoStdInit {
    public void init() {
        config = new ConfigUtil("config/", "ModMdo.mhf", entrust).setNote("""
                this file is database file of "ModMdo"
                not configs only
                so this file maybe get large and large
                but usually, it will smaller than 1MB
                                
                """);

        languageDictionary = new LanguageDictionary("/format/format.json");

        new HereCommand().register();
        new DimensionHereCommand().register();
        new ModMdoUserCommand().register();
        new ServerTickListener().listener();
        new ServerStartListener().listener();
    }
}
