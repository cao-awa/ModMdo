package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.backup.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.shilohrien.databse.increment.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import net.minecraft.command.*;
import net.minecraft.server.command.*;

import java.text.*;
import java.util.*;

import static net.minecraft.server.command.CommandManager.*;

public class ArchiveCommand extends SimpleCommand {
    @Override
    public SimpleCommand register() {
        SharedVariables.commandRegister.register(literal("archive").then(literal("vcs").executes(infos -> {
            return 0;
        }).then(literal("build").executes(build -> {
            EntrustParser.thread(() -> {
                buildArchive(build, false);
            }).start();
            return 0;
        }).then(literal("subscribe").executes(buildAndSub -> {
            EntrustParser.thread(() -> {
                buildArchive(buildAndSub, true);
            }).start();
            return 0;
        })))).then(literal("archives").then(argument("name", StringArgumentType.string()).suggests((context, builder) -> {
            Archiver archiver = createArchive(context);
            return CommandSource.suggestMatching(archiver.getMetadata().keySet(), builder);
        }).then(literal("information").executes(info -> {
            String name = StringArgumentType.getString(info, "name");
            Archiver archiver = createArchive(info, name);
            IncrementDataTable<String> metadata = archiver.getMetadata();
            if (metadata == null) {
                sendFeedback(info, TextUtil.translatable("modmdo.archive.not_found"));
                return - 1;
            }
            String algorithm = Objects.toString(metadata.query(name, "algorithm"));
            long size = Long.parseLong((Objects.requireNonNull(metadata.query(name, "size"))).toString()) / 1024 / 1024;
            long files = Long.parseLong(Objects.requireNonNull(metadata.query(name, "files")).toString());
            sendFeedback(info, TextUtil.translatable("modmdo.archive.information", name, algorithm, size, files));
            return 0;
        })).then(literal("restore").executes(restore -> {
            EntrustParser.thread(() -> restoreArchive(restore, StringArgumentType.getString(restore, "name"))).start();
            return 0;
        })).then(literal("delete").executes(delete -> {
            EntrustParser.thread(() -> {
                Archiver archiver = createArchive(delete);
                archiver.delete();
            }).start();
            return 0;
        })))));
        return this;
    }

    public void buildArchive(CommandContext<ServerCommandSource> source, boolean subscribe) {
        String path = SharedVariables.getServerLevelPath(source.getSource().getServer());
        String name = SharedVariables.getServerLevelName(source.getSource().getServer());
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd_hh_mm_ss");
        Calendar calendar = Calendar.getInstance();
        Archiver.archive(path, "archives/modmdo/", name, date.format(calendar.getTime()), source.getSource(), subscribe);
    }

    public void restoreArchive(CommandContext<ServerCommandSource> source, String archiveName) {
        String path = SharedVariables.getServerLevelPath(source.getSource().getServer());
        String name = SharedVariables.getServerLevelName(source.getSource().getServer());
        Archiver.restore(path, "archives/modmdo/", name, archiveName, source.getSource());
    }

    public Archiver createArchive(CommandContext<ServerCommandSource> source, String archiveName) {
        String path = SharedVariables.getServerLevelPath(source.getSource().getServer());
        String name = SharedVariables.getServerLevelName(source.getSource().getServer());
        return Archiver.create(path, "archives/modmdo/", name, archiveName, source.getSource());
    }

    public Archiver createArchive(CommandContext<ServerCommandSource> source) {
        String path = SharedVariables.getServerLevelPath(source.getSource().getServer());
        String name = SharedVariables.getServerLevelName(source.getSource().getServer());
        return Archiver.create(path, "archives/modmdo/", name, null, source.getSource());
    }
}
