package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.utils.times.TimeType;
import com.github.zhuaidadaya.modmdo.utils.times.Times;
import com.github.zhuaidadaya.modmdo.bak.Backup;
import com.github.zhuaidadaya.modmdo.bak.BackupUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import org.json.JSONObject;

import java.io.File;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BackupCommand extends SimpleCommandOperation implements ConfigurableCommand {
    @Override
    public void register() {
        init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("backup").executes(defaultBackup -> {
                if(commandApplyToPlayer(1, getPlayer(defaultBackup), this, defaultBackup)) {
                    backup(null, dedicated, defaultBackup.getSource());
                }
                return 0;
            }).then(literal("name").then(argument("asName", StringArgumentType.string()).executes(asNameBackup -> {
                if(commandApplyToPlayer(1, getPlayer(asNameBackup), this, asNameBackup)) {
                    backup(asNameBackup.getInput().split(" ")[1], dedicated, asNameBackup.getSource());
                }
                return 1;
            }))).then(literal("stop").executes(stop -> {
                if(commandApplyToPlayer(1, getPlayer(stop), this, stop)) {
                    stopBackup(stop.getSource());
                }
                return - 1;
            })).then(literal("status").executes(status -> {
                if(commandApplyToPlayer(1, getPlayer(status), this, status)) {
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
            String levelName = getServerLevelNamePath(source.getServer());
            String sourcePath = getServerLevelPath(source.getServer());

            TranslatableText result;
            result = new TranslatableText("backup.starting");

            try {
                if(! bak.isSynchronizing()) {
                    for(ServerPlayerEntity player : players.getPlayerList()) {
                        if(commandApplyToPlayer(1, player, this, source)) {
                            player.sendMessage(result, false);
                        }
                    }

                    source.sendFeedback(new TranslatableText("backup.running"), false);
                    result = bak.createBackup(new Backup(name, "backup/" + levelName + "/" + Times.getTime(TimeType.AS_SECOND), sourcePath));

                    for(ServerPlayerEntity player : players.getPlayerList()) {
                        if(commandApplyToPlayer(1, player, this, source)) {
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

        updateBackups();
    }
}
