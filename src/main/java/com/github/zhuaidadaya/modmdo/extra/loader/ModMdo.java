package com.github.zhuaidadaya.modmdo.extra.loader;

import com.github.zhuaidadaya.modmdo.commands.*;
import com.github.zhuaidadaya.modmdo.commands.argument.*;
import com.github.zhuaidadaya.modmdo.event.*;
import com.github.zhuaidadaya.modmdo.event.trigger.*;
import com.github.zhuaidadaya.modmdo.event.variable.*;
import com.github.zhuaidadaya.modmdo.lang.*;
import com.github.zhuaidadaya.modmdo.reads.*;
import com.github.zhuaidadaya.modmdo.resourceLoader.*;
import com.github.zhuaidadaya.modmdo.simple.vec.*;
import com.github.zhuaidadaya.modmdo.utils.dimension.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.ObjectConfigUtil;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.entity.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.*;
import net.minecraft.text.*;
import org.json.*;

import java.io.*;
import java.nio.charset.*;

import static com.github.zhuaidadaya.modmdo.ModMdoStdInitializer.initModMdoVariables;
import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

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
        ModMdoEventCenter.registerEntityDeath(event -> {
            EntrustExecution.tryTemporary(() -> {
                LivingEntity entity = event.getTargeted().get(0);
                if (entity instanceof ServerPlayerEntity player) {
                    if (isUserDeadMessageReceive(player.getUuid()) & enableDeadMessage) {
                        String dimension = DimensionUtil.getDimension(player);
                        TranslatableText text = new TranslatableText("dead.deadIn", DimensionUtil.getDimensionColor(dimension), DimensionUtil.getDimensionName(dimension), new XYZ(event.getPos()).getIntegerXYZ());
                        if (player.deathTime == 1) {
                            player.sendMessage(text, false);
                        }
                    }
                }
            });
        });

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
