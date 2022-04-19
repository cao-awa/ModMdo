package com.github.zhuaidadaya.modmdo.extra.loader;

import com.github.zhuaidadaya.modmdo.commands.ModMdoUserCommand;
import com.github.zhuaidadaya.modmdo.reads.FileReads;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resources;
import com.github.zhuaidadaya.modmdo.utils.config.ObjectConfigUtil;
import net.minecraft.server.MinecraftServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static com.github.zhuaidadaya.modmdo.ModMdoStdInitializer.initModMdoVariables;
import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdo extends ModMdoExtra {
    public void init() {
        config = new ObjectConfigUtil(entrust, getServerLevelPath((MinecraftServer) getArgs().get("server")), "modmdo.mhf");

        allDefault();

        new ModMdoUserCommand().init();

        try {
            initModMdoVariables();
        } catch (Exception e) {

        }
        updateModMdoVariables();
    }
}
