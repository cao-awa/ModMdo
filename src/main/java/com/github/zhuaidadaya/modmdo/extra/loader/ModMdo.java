package com.github.zhuaidadaya.modmdo.extra.loader;

import com.github.zhuaidadaya.modmdo.commands.*;
import com.github.zhuaidadaya.modmdo.commands.argument.*;
import com.github.zhuaidadaya.modmdo.event.*;
import com.github.zhuaidadaya.modmdo.simple.vec.*;
import com.github.zhuaidadaya.modmdo.utils.dimension.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.ObjectConfigUtil;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.entity.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import static com.github.zhuaidadaya.modmdo.ModMdoStdInitializer.initModMdoVariables;
import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

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
        ModMdoEventCenter.registerPlayerDeath(event -> {
            EntrustExecution.tryTemporary(() -> {
                LivingEntity entity = event.getEntity();
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
    }

    public boolean needEnsure() {
        return true;
    }
}
