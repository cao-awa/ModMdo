package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.MCH.times.TimeType;
import com.github.zhuaidadaya.MCH.times.Times;
import com.github.zhuaidadaya.modMdo.bak.Backup;
import com.github.zhuaidadaya.modMdo.bak.BackupUtil;
import com.github.zhuaidadaya.modMdo.mixins.MinecraftServerSession;
import com.github.zhuaidadaya.utils.config.Config;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import java.io.File;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BackupCommand extends SimpleCommandOperation implements ConfigurableCommand {
    public void register() {
        init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("backup").executes(defaultBackup -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_BAK, getPlayer(defaultBackup), this, defaultBackup)) {
                    backup(null, dedicated, defaultBackup.getSource());
                }
                return 0;
            }).then(literal("name").then(argument("asName", StringArgumentType.string()).executes(asNameBackup -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_BAK, getPlayer(asNameBackup), this, asNameBackup)) {
                    backup(asNameBackup.getInput().split(" ")[1], dedicated, asNameBackup.getSource());
                }
                return 1;
            }))).then(literal("stop").executes(stop -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_BAK, getPlayer(stop), this, stop)) {
                    stopBackup(stop.getSource());
                }
                return - 1;
            })).then(literal("status").executes(status -> {
                if(commandApplyToPlayer(MODMDO_COMMAND_BAK, getPlayer(status), this, status)) {
                    sendFeedback(status, bak.isSynchronizing() ? new TranslatableText("backup.running") : new TranslatableText("backup.no.task"));
                }
                return 2;
            })));
        });
    }

    public void stopBackup(ServerCommandSource source) {
        new Thread(() -> {
            if(bak.isSynchronizing()) {
                source.sendFeedback(new TranslatableText("backup.stopping"), false);
                source.sendFeedback(bak.stop(), false);
            } else {
                source.sendFeedback(new TranslatableText("backup.no.task"), false);
            }
        }).start();
    }

    public void backup(String name, boolean dedicated, ServerCommandSource source) {
        new Thread(() -> {
            PlayerManager players = source.getServer().getPlayerManager();
            String levelName = ((MinecraftServerSession) source.getServer()).getSession().getDirectoryName() + "/";
            String sourcePath = (dedicated ? "" : "saves/") + levelName;

            TranslatableText result;
            result = new TranslatableText("backup.starting");

            try {
                if(! bak.isSynchronizing()) {
                    for(ServerPlayerEntity player : players.getPlayerList()) {
                        if(commandApplyToPlayer(MODMDO_COMMAND_BAK, player, this, source)) {
                            player.sendMessage(result, false);
                        }
                    }

                    source.sendFeedback(new TranslatableText("backup.running"), false);
                    result = bak.createBackup(new Backup(name, "backup/" + levelName + "/" + Times.getTime(TimeType.AS_SECOND), sourcePath));

                    for(ServerPlayerEntity player : players.getPlayerList()) {
                        if(commandApplyToPlayer(MODMDO_COMMAND_BAK, player, this, source)) {
                            player.sendMessage(result, false);
                        }
                    }
                } else {
                    source.sendError(result);

                    result = new TranslatableText("backup.tasking");

                    source.sendError(result);
                }
            } catch (Exception e) {

            }
        }).start();
    }

    public void init() {
        LOGGER.info("initializing backups");
        Object backupsConf = config.getConfig("backups");

        if(backupsConf != null) {
            JSONObject backups = new JSONObject(backupsConf.toString());
            JSONObject backupsCopy = new JSONObject(backups.toString());

            try {
                for(Object o : backupsCopy.keySet()) {
                    JSONObject backup = backups.getJSONObject(o.toString());

                    String path = backup.get("path").toString();
                    File backupPtah = new File(path);

                    try {
                        if(! backupPtah.exists() | ! backupPtah.isDirectory() | backupPtah.listFiles().length < 1) {
                            backups.remove(o.toString());
                        }
                    } catch (Exception e) {
                        backups.remove(o.toString());
                    }
                }
            } catch (Exception e) {

            }

            bak = new BackupUtil(backups);
        } else {
            bak = new BackupUtil();
            config.set("backups", new JSONObject());
        }
        LOGGER.info("initialized backups");

        updateBackups();
    }
}
