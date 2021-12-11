package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.MCH.times.TimeType;
import com.github.zhuaidadaya.MCH.times.Times;
import com.github.zhuaidadaya.MCH.utils.config.Config;
import com.github.zhuaidadaya.modMdo.bak.Backup;
import com.github.zhuaidadaya.modMdo.bak.BackupUtil;
import com.github.zhuaidadaya.modMdo.mixins.MinecraftServerSession;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BackupCommand {
    public void register() {
        initBackups();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("backup").executes(defaultBackup -> {
                ServerCommandSource source = defaultBackup.getSource();
                PlayerManager players = source.getServer().getPlayerManager();
                String levelName = ((MinecraftServerSession) defaultBackup.getSource().getServer()).getSession().getDirectoryName() + "/";
                String sourcePath = (dedicated ? "" : "saves/") + levelName;

                TranslatableText result;
                result = new TranslatableText("backup.starting");
                for(ServerPlayerEntity player : players.getPlayerList()) {
                    player.sendMessage(result,false);
                }

                if(!bak.isSynchronizing()) {
                    result = bak.createBackup(new Backup(null, "backup/" + levelName + "/" + Times.getTime(TimeType.AS_SECOND), sourcePath));
                } else {
                    result = new TranslatableText("backup.tasking");
                }

                for(ServerPlayerEntity player : players.getPlayerList()) {
                    player.sendMessage(result,false);
                }

                return 0;
            }).then(argument("asName", StringArgumentType.string()).executes(asNameBackup -> {

                return 1;
            })));
        });
    }

    public void initBackups() {
        LOGGER.info("initializing backups");
        Config<Object, Object> backupsConf = config.getConfig("backups");
        if(backupsConf != null) {
            bak = new BackupUtil(new JSONObject(backupsConf.getValue()));
        } else {
            bak = new BackupUtil();
            config.set("backups", new JSONObject());
        }
        LOGGER.info("initialized backups");

        updateBackups();
    }
}
