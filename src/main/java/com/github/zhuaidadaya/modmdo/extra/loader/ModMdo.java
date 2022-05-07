package com.github.zhuaidadaya.modmdo.extra.loader;

import com.github.zhuaidadaya.modmdo.commands.ModMdoUserCommand;
import com.github.zhuaidadaya.rikaishinikui.handler.config.ObjectConfigUtil;
import net.minecraft.server.MinecraftServer;

import static com.github.zhuaidadaya.modmdo.ModMdoStdInitializer.initModMdoVariables;
import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdo extends ModMdoExtra {
    public void init() {
        config = new ObjectConfigUtil(entrust, getServerLevelPath((MinecraftServer) getArgs().get("server")), "modmdo.mhf");

        allDefault();
        defaultConfig();

        new ModMdoUserCommand().init();

        try {
            initModMdoVariables();
        } catch (Exception e) {

        }
        updateModMdoVariables();
    }
}
