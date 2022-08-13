package com.github.cao.awa.modmdo.backup;

import com.github.cao.awa.modmdo.annotations.*;
import com.github.cao.awa.modmdo.mixins.server.*;
import com.github.cao.awa.modmdo.mixins.server.chunk.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.file.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.cao.awa.shilohrien.databse.*;
import com.github.cao.awa.shilohrien.databse.increment.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.annotaions.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.command.*;
import net.minecraft.server.world.*;

import java.io.*;
import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@ModMdo
@SingleThread
public class Archiver {
    private static final Object2ObjectOpenHashMap<String, IncrementDatabase<String>> databases = new Object2ObjectOpenHashMap<>();
    public static boolean restoring = false;
    public static boolean archiving = false;
    private final String source;
    private final String to;
    private final String main;
    private final String base;
    private final String archiveName;
    private final MessageDigger.Sha algorithm;
    private final IncrementDatabase<String> database;
    private final ServerCommandSource commandSource;
    private final IncrementDataTable<String> metadata;
    private final boolean subscribe;
    private IncrementDataTable<String> table;
    private boolean cancel;
    private long size;
    private int files;

    public Archiver(String source, String to, MessageDigger.Sha algorithm, IncrementDatabase<String> database, String archiveName, ServerCommandSource commandSource, boolean subscribe) {
        this.source = source;
        this.base = to + "/vcs/";
        this.to = to + "/vcs/" + archiveName;
        this.main = to + "/vcs/main";
        this.algorithm = algorithm;
        this.database = database;
        this.archiveName = archiveName;
        this.commandSource = commandSource;
        this.subscribe = subscribe;

        if (database.getTable("metadata") == null) {
            database.addTable(new IncrementDataTable<>("metadata", new DataBody().add("time").add("files").add("size").add("algorithm"), new MainKeyActor.KeepMainKeyTactics<>()));
        }
        metadata = database.getTable("metadata");
    }

    public static void archive(String source, String to, String levelName, String archiveName, ServerCommandSource commandSource, boolean subscribe) {
        IncrementDatabase<String> database = databases.get(levelName);
        if (database == null) {
            database = IncrementDatabase.load(archiveName, to + "/" + levelName + "/backup.db", true);
        }
        databases.put(levelName, database);
        new Archiver(source, to + "/" + levelName + "/", MessageDigger.Sha3.SHA_256, database, archiveName, commandSource, subscribe).build();
    }

    public static void main(String[] args) {
    }

    public static void restore(String source, String to, String levelName, String archiveName, ServerCommandSource commandSource) {
        IncrementDatabase<String> database = databases.get(levelName);
        if (database == null) {
            database = IncrementDatabase.load(archiveName, to + "/" + levelName + "/backup.db", true);
        }
        databases.put(levelName, database);
        new Archiver(source, to + "/" + levelName + "/", MessageDigger.Sha3.SHA_256, database, archiveName, commandSource, false).restore();
    }

    public void restore() {
        if (restoring) {
            return;
        }
        restoring = true;

        commandSource.getServer().getPlayerManager().getPlayerList().forEach(player -> player.networkHandler.connection.send(new DisconnectS2CPacket(minecraftTextFormat.format(loginUsers.getUser(player), "modmdo.archive.restoring").text())));

        setSave(false);
        commandSource.getServer().getWorlds().forEach(world -> {
            ((ThreadedAnvilChunkStorageInterface)world.getChunkManager().threadedAnvilChunkStorage).getCurrentChunkHolders().clear();
            ((ThreadedAnvilChunkStorageInterface)world.getChunkManager().threadedAnvilChunkStorage).getChunkHolders().clear();
            ((ThreadedAnvilChunkStorageInterface)world.getChunkManager().threadedAnvilChunkStorage).getChunksToUnload().clear();
            ((ServerChunkManagerInterface)world.getChunkManager()).initChunkCaches();

            while(world.getChunkManager().getLoadedChunkCount() > 0) {
                TimeUtil.coma(10);
            }
        });

        IncrementDataTable<String> subTable = database.getTable(archiveName);

        table = database.getTable("main");

        if (table == null) {
            setSave(true);
            restoring = false;
            return;
        }

        for (String to : table.keySet()) {
            if (cancel) {
                return;
            }
            File toFile = new File(to);
            try {
                if (subTable != null) {
                    if (subTable.query(to) != null) {
                        continue;
                    }
                }
                File sourceFile = new File(Objects.toString(table.query(to, "path")));
                String sourceSha = Objects.toString(table.query(to, "sha"));
                String toSha = FileUtil.fileSha(toFile, algorithm);
                if (Objects.equals(sourceSha, toSha)) {
                    continue;
                }
                FileUtil2.copy(sourceFile, toFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (subTable == null) {
            setSave(true);
            restoring = false;
            return;
        }

        for (String to : subTable.keySet()) {
            if (cancel) {
                setSave(true);
                restoring = false;
                return;
            }
            File toFile = new File(to);
            try {
                File sourceFile = new File(Objects.toString(subTable.query(to, "path")));
                String sourceSha = Objects.toString(subTable.query(to, "sha"));
                String toSha = FileUtil.fileSha(toFile, algorithm);
                if (Objects.equals(sourceSha, toSha)) {
                    continue;
                }
                FileUtil2.copy(sourceFile, toFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setSave(true);
        restoring = false;
    }

    public static Archiver create(String source, String to, String levelName, String archiveName, ServerCommandSource commandSource) {
        IncrementDatabase<String> database = databases.get(levelName);
        if (database == null) {
            database = IncrementDatabase.load(archiveName == null ? "*" : archiveName, to + "/" + levelName + "/backup.db", true);
        }
        if (archiveName != null) {
            databases.put(levelName, database);
        }
        return new Archiver(source, to + "/" + levelName + "/", MessageDigger.Sha3.SHA_256, database, archiveName, commandSource, false);
    }

    private void setSave(boolean save) {
        {
            for (ServerWorld serverWorld : commandSource.getServer().getWorlds()) {
                if (serverWorld != null) {
                    serverWorld.savingDisabled = ! save;
                }
            }
        }
    }

    public void build() {
        if (archiving) {
            return;
        }
        commandSource.sendFeedback(minecraftTextFormat.format("modmdo.archive.building").text(), false);
        commandSource.getServer().save(false, true, true);
        setSave(false);
        archiving = true;
        if (database != null) {
            boolean hasMain = true;
            if (database.getTable("main") == null) {
                notifyToSubscribe(minecraftTextFormat.formatted("modmdo.archive.notify.create.database"));
                IncrementDataTable<String> table = new IncrementDataTable<>("main", DataBody.body().add("sha").add("path"), new MainKeyActor.KeepMainKeyTactics<>());
                table.setIncremental(false);
                database.addTable(table);
                hasMain = false;
            }
            table = database.getTable("main");
            {
                if (hasMain) {
                    if (database.getTable(archiveName) == null) {
                        IncrementDataTable<String> table = new IncrementDataTable<>(archiveName, DataBody.body().add("sha").add("path"), new MainKeyActor.KeepMainKeyTactics<>());
                        table.setIncremental(false);
                        database.addTable(table, true);
                    }
                }
            }
            archive(source, main, true);
            metadata.update(archiveName, DataBody.body().add(archiveName).add(files).add(size).add(algorithm.instanceName()));
            database.save();
        } else {
            archive(source, to, false);
        }
        if (cancel) {
            delete();
            commandSource.sendFeedback(minecraftTextFormat.format("modmdo.archive.build.canceled").text(), false);
        } else {
            commandSource.sendFeedback(minecraftTextFormat.format("modmdo.archive.build.done").text(), false);
        }
        setSave(true);
        archiving = false;
    }

    private void notifyToSubscribe(String text) {
        if (subscribe) {
            SharedVariables.sendMessageToAllPlayer(commandSource.getServer(), TextUtil.literal(text).text(), true);
        }
    }

    public void cancel() {
        this.cancel = true;
    }

    private void archive(String from, String to, boolean isMain) {
        if (cancel) {
            return;
        }
        from = from.replace("//", "/");
        from = from.replace("\\", "/");
        for (File sourceFile : Objects.requireNonNull(new File(from).listFiles())) {
            if (cancel) {
                return;
            }
            try {
                if (sourceFile.isDirectory()) {
                    if (Objects.requireNonNull(sourceFile.listFiles()).length > 0) {
                        archive(from + "/" + sourceFile.getName(), to, isMain);
                    }
                } else if (sourceFile.isFile()) {
                    notifyToSubscribe(minecraftTextFormat.formatted("modmdo.archive.notify.processing.file", sourceFile.getPath()));
                    String oldFile = Objects.toString(table.query(sourceFile.getAbsolutePath(), "path"));
                    String sourceSha = FileUtil.fileSha(sourceFile, algorithm);
                    File toFile = new File(to + "/" + sourceSha);
                    String toSha;
                    toSha = Objects.toString(table.query(sourceFile.getAbsolutePath(), "sha"));
                    if (toSha == null || toSha.equals("null") && toFile.isFile()) {
                        toSha = FileUtil.fileSha(toFile, algorithm);
                    }
                    if (Objects.equals(sourceSha, toSha)) {
                        continue;
                    }

                    if (isMain) {
                        affect(sourceFile.getAbsolutePath());
                        table.update(sourceFile.getAbsolutePath(), "sha", sourceSha);
                        table.update(sourceFile.getAbsolutePath(), "path", toFile.getAbsolutePath());
                    }
                    size += sourceFile.length();
                    files++;
                    FileUtil2.deleteFile(oldFile);
                    FileUtil2.copy(sourceFile, toFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void affect(String source) {
        for (String tableName : database.getTables().keySet()) {
            if (tableName.equals("metadata")) {
                continue;
            }
            IncrementDataTable<String> subTable = database.getTable(tableName);
            if (subTable == null || subTable == this.table) {
                continue;
            }
            Object o = subTable.query(source);
            if (o != null) {
                continue;
            }
            File sourceFile = new File(source);
            String mainSha = Objects.toString(table.query(source, "sha"));
            File toFile = new File(base + "/" + tableName + "/" + mainSha);
            if (toFile.isFile()) {
                continue;
            }
            File copyFile = new File(Objects.toString(this.table.query(source, "path")));
            String toSha = FileUtil.fileSha(toFile, algorithm);
            if (toSha.equals("null")) {
                toSha = FileUtil.fileSha(toFile, algorithm);
            }
            subTable.update(sourceFile.getAbsolutePath(), "sha", mainSha);
            subTable.update(sourceFile.getAbsolutePath(), "path", toFile.getAbsolutePath());
            if (Objects.equals(mainSha, toSha)) {
                continue;
            }
            EntrustExecution.tryTemporary(() -> FileUtil2.copy(copyFile, toFile));
        }
    }

    public IncrementDataTable<String> getMetadata() {
        return metadata;
    }

    public IncrementDatabase<String> getDatabase() {
        return database;
    }

    public void delete() {
        archiving = true;
        FileUtil.deleteFiles(to);
        archiving = false;
    }
}
