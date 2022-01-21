package com.github.zhuaidadaya.modMdo;

import com.github.zhuaidadaya.modMdo.reads.FileReads;
import com.github.zhuaidadaya.modMdo.resourceLoader.Resources;
import com.github.zhuaidadaya.modMdo.type.ModMdoType;
import net.fabricmc.api.DedicatedServerModInitializer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

public class ModMdoServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        new Thread(() -> {
            Thread.currentThread().setName("ModMdo");

            LOGGER.info("loading for ModMdo Server (step 2/2)");

            modMdoType = ModMdoType.SERVER;

            parseMapFormat();
            //        new ProjectCommand().register();


            if(modMdoToken != null)
                saveToken();
        }).start();
    }

    public void parseMapFormat() {
        JSONObject commandMap = new JSONObject(FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource("/assets/modmdo/format/command_map.json", getClass())))));
        JSONObject versionMap = new JSONObject(FileReads.read(new BufferedReader(new InputStreamReader(Resources.getResource("/assets/modmdo/format/versions_map.json", getClass())))));

        for(String s : versionMap.keySet())
            modMdoIdToVersionMap.put(Integer.valueOf(s), versionMap.getString(s));

        for(String s : versionMap.keySet())
            modMdoVersionToIdMap.put(versionMap.getString(s), Integer.valueOf(s));

        for(String s : commandMap.keySet())
            modMdoCommandVersionMap.put(s, commandMap.getInt(s));
    }
}
