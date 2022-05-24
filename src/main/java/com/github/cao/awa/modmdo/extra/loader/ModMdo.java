package com.github.cao.awa.modmdo.extra.loader;

import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.commands.argument.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.variable.*;
import com.github.cao.awa.modmdo.reads.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.server.*;
import org.json.*;

import java.io.*;

import static com.github.cao.awa.modmdo.ModMdoStdInitializer.*;
import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdo extends ModMdoExtra<ModMdo> {
    private MinecraftServer server;

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public void init() {
        config = new ObjectConfigUtil(entrust, getServerLevelPath(getServer()), "modmdo.mhf");

        allDefault();
        defaultConfig();

        try {
            initModMdoVariables();
        } catch (Exception e) {

        }

        updateModMdoVariables();
    }

    public void initStaticCommand() {
    }

    public void initCommand() {
        try {
            new ModMdoUserCommand().register().init();
            new HereCommand().register();
            new DimensionHereCommand().register();
            new CavaCommand().register();
            new RankingCommand().register();
            new TestCommand().register();
            new TemporaryWhitelistCommand().register();
            new ModMdoCommand().register();

            ArgumentInit.init();
        } catch (Exception e) {

        }
    }

    public void initEvent() {
        triggerBuilder = new ModMdoTriggerBuilder();
        EntrustExecution.tryTemporary(() -> {
            new File("config/modmdo/resources/events/").mkdirs();

            EntrustExecution.tryFor(EntrustParser.getNotNull(new File("config/modmdo/resources/events/").listFiles(), new File[0]), file -> {
                EntrustExecution.tryTemporary(() -> {
                    triggerBuilder.register(new JSONObject(FileReads.read(new BufferedReader(new FileReader(file)))), file);
                    LOGGER.info("Registered event: " + file.getPath());
                }, ex -> {
                    LOGGER.warn("Failed register event: " + file.getPath(), ex);
                });
            });
        });

        variables.clear();
        variableBuilder = new ModMdoVariableBuilder();
        EntrustExecution.tryTemporary(() -> {
            new File("config/modmdo/resources/persistent/").mkdirs();

            EntrustExecution.tryFor(EntrustParser.getNotNull(new File("config/modmdo/resources/persistent/").listFiles(), new File[0]), file -> {
                EntrustExecution.notNull(variableBuilder.build(file, new JSONObject(FileReads.read(new BufferedReader(new FileReader(file))))), v -> {
                    variables.put(v.getName(), v);
                });
            });
        });
    }

    public boolean needEnsure() {
        return true;
    }
}
