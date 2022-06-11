package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.certificate.pass.*;
import com.github.cao.awa.modmdo.commands.argument.ban.*;
import com.github.cao.awa.modmdo.commands.argument.whitelist.*;
import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;
import net.minecraft.text.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class TemporaryCommand extends SimpleCommand {
    @Override
    public TemporaryCommand register() {
        SharedVariables.commandRegister.register(literal("temporary").requires(e -> e.hasPermissionLevel(4)).then(literal("ban").then(literal("add").then(argument("target", ModMdoWhitelistArgumentType.whitelist()).then(argument("minutes", IntegerArgumentType.integer(1)).executes(ban -> {
            return ban(ban, ModMdoWhitelistArgumentType.getWhiteList(ban, "target").name, IntegerArgumentType.getInteger(ban, "minutes"));
        })).then(literal("-1").executes(ban -> {
            return ban(ban, ModMdoWhitelistArgumentType.getWhiteList(ban, "target").name, - 1);
        })).then(literal("60").executes(ban -> {
            return ban(ban, ModMdoWhitelistArgumentType.getWhiteList(ban, "target").name, 60);
        })))).then(literal("remove").then(argument("target", ModMdoTemporaryBanArgumentType.banned()).executes(remove -> {
            Certificate certificate = ModMdoTemporaryBanArgumentType.getCertificate(remove, "target");
            banned.remove(certificate.getName());
            saveVariables();
            sendFeedback(getPlayer(remove), new TranslatableText("modmdo.ban.pardon", certificate.getName()));
            return 0;
        }))).then(literal("list").executes(list -> {
            showTemporaryBan(list);
            SharedVariables.updateTemporaryBanNames(getServer(list), true);
            return 0;
        }))).then(literal("pass").then(literal("add").then(argument("target", StringArgumentType.string()).then(argument("minutes", IntegerArgumentType.integer(1)).executes(ban -> {
            String name = StringArgumentType.getString(ban, "target");
            int minute = IntegerArgumentType.getInteger(ban, "minutes");
            return 0;
        })).then(literal("-1").executes(ban -> {
            String name = StringArgumentType.getString(ban, "target");
            return 0;
        })).then(literal("60").executes(ban -> {
            String name = StringArgumentType.getString(ban, "target");
            return 0;
        })))).then(literal("remove").then(argument("target", ModMdoPassArgumentType.pass()).executes(remove -> {
            Certificate certificate = ModMdoTemporaryBanArgumentType.getCertificate(remove, "target");
            temporaryPass.remove(certificate.getName());
            sendFeedback(getPlayer(remove), new TranslatableText("modmdo.pass.cancel", certificate.getName()));
            return 0;
        }))).then(literal("list").executes(list -> {
            showTemporaryBan(list);
            SharedVariables.updateTemporaryBanNames(getServer(list), true);
            return 0;
        }))).then(literal("whitelist").then(literal("add").then(argument("name", StringArgumentType.string()).executes(addDefault -> {
            whitelist(addDefault, TemporaryPass.empty());
            return 0;
        }))).then(literal("list").executes(showTemporary -> {
            showTemporaryWhitelist(showTemporary);
            SharedVariables.updateTemporaryWhitelistNames(getServer(showTemporary), true);
            return 0;
        })).then(literal("remove").then(argument("name", ModMdoTemporaryWhitelistArgumentType.whitelist()).executes(remove -> {
            TemporaryCertificate wl = ModMdoTemporaryWhitelistArgumentType.getWhiteList(remove, "name");
            if (SharedVariables.temporaryWhitelist.containsName(wl.getName())) {
                SharedVariables.temporaryWhitelist.remove(wl.getName());
                sendFeedback(remove, new TranslatableText("temporary.whitelist.removed", wl.getName()));
                SharedVariables.updateTemporaryWhitelistNames(getServer(remove), true);
                return 0;
            }
            sendError(remove, new TranslatableText("arguments.temporary.whitelist.not.registered", wl.getName()));
            return - 1;
        })))).then(literal("connection").then(literal("whitelist").executes(whitelist -> {
            if (SharedVariables.modmdoConnectionAccepting.isValid()) {
                long million = SharedVariables.modmdoConnectionAccepting.getMillions() - TimeUtil.processMillion(SharedVariables.modmdoConnectionAccepting.getRecording());
                long minute = TimeUtil.processRemainingMinutes(million);
                long second = TimeUtil.processRemainingSeconds(million);
                sendFeedback(whitelist, new TranslatableText("connection.whitelist.accepting", minute, second));
            } else {
                sendFeedback(whitelist, new TranslatableText("connection.whitelist.no.accepting"));
            }
            return 0;
        }).then(literal("accept").then(literal("one").executes(acceptOne -> {
            if (SharedVariables.modmdoConnectionAccepting.isValid()) {
                long million = SharedVariables.modmdoConnectionAccepting.getMillions() - TimeUtil.processMillion(SharedVariables.modmdoConnectionAccepting.getRecording());
                long minute = TimeUtil.processRemainingMinutes(million);
                long second = TimeUtil.processRemainingSeconds(million);
                sendError(acceptOne, new TranslatableText("connection.whitelist.accepting", minute, second));
            } else {
                sendFeedback(acceptOne, new TranslatableText("connection.whitelist.accepting.one"));
                SharedVariables.modmdoConnectionAccepting = new TemporaryCertificate("", TimeUtil.millions(), 1000 * 60 * 5);
            }
            return 0;
        }))).then(literal("cancel").executes(cancel -> {
            if (SharedVariables.modmdoConnectionAccepting.isValid()) {
                SharedVariables.modmdoConnectionAccepting = new TemporaryCertificate("", - 1, - 1);
            } else {
                sendError(cancel, new TranslatableText("connection.whitelist.no.accepting"));
            }
            return 0;
        })))));
        return this;
    }

    public void showTemporaryWhitelist(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        SharedVariables.flushTemporaryWhitelist();
        ServerPlayerEntity player = getPlayer(source);
        if (SharedVariables.temporaryWhitelist.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (TemporaryCertificate wl : SharedVariables.temporaryWhitelist.values()) {
                builder.append(wl.getName()).append(": ");
                builder.append(wl.formatRemaining());
                builder.append("\n");
            }
            builder.delete(builder.length() - 1, builder.length());
            sendMessage(player, new TranslatableText("commands.temporary.whitelist.list", SharedVariables.temporaryWhitelist.size(), builder.toString()), false);
        } else {
            sendMessage(player, new TranslatableText("commands.temporary.whitelist.none"), false);
        }
    }

    public int whitelist(CommandContext<ServerCommandSource> source, Pass pass) {
        String name = StringArgumentType.getString(source, "name");
        if (SharedVariables.temporaryWhitelist.containsName(name)) {
            sendFeedback(source, new TranslatableText("temporary.whitelist.add.already.is.whitelist", name));
            return - 1;
        }
        if (SharedVariables.whitelist.containsName(name)) {
            sendFeedback(source, new TranslatableText("modmdo.whitelist.add.already.is.whitelist", name));
            return - 1;
        }
        temporaryWhitelist(name, 1000 * 60 * 5);
        sendFeedback(source, new TranslatableText("temporary.whitelist.add.default", name));
        SharedVariables.updateTemporaryWhitelistNames(getServer(source), true);
        return 0;
    }

    public void showTemporaryBan(CommandContext<ServerCommandSource> source) {
        try {
            SharedVariables.flushTemporaryBan();
            ServerPlayerEntity player = getPlayer(source);
            if (banned.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (Certificate ban : banned.values()) {
                    if (ban instanceof TemporaryCertificate timeLimit) {
                        builder.append(timeLimit.getName()).append(": ");
                        builder.append(timeLimit.formatRemaining());
                        builder.append("\n");
                    } else {
                        builder.append(ban.getName()).append(": ");
                        builder.append(SharedVariables.consoleTextFormat.format("temporary.ban.indefinite"));
                        builder.append("\n");
                    }
                }
                builder.delete(builder.length() - 1, builder.length());
                sendMessage(player, new TranslatableText("commands.temporary.ban.list", banned.size(), builder.toString()), false);
            } else {
                sendMessage(player, new TranslatableText("commands.temporary.ban.none"), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void temporaryWhitelist(String name, long millions) {
        SharedVariables.temporaryWhitelist.put(name, new TemporaryCertificate(name, TimeUtil.millions(), millions));
    }

    public int ban(CommandContext<ServerCommandSource> ban, String name, int minutes) {
        ServerPlayerEntity player = getServer(ban).getPlayerManager().getPlayer(name);
        force.add(player);
        Certificate certificate = ModMdoWhitelistArgumentType.getWhiteList(ban, "target");
        Certificate banned = SharedVariables.banned.get(name);
        boolean already = false;
        if (banned == null) {
            temporaryBan(name, certificate, minutes == - 1 ? - 1 : minutes * 1000L * 60L, false);
            banned = SharedVariables.banned.get(name);
        } else {
            already = true;
        }
        if (banned instanceof TemporaryCertificate temp) {
            if (already) {
                temporaryBan(name, certificate, minutes == - 1 ? - 1 : minutes * 1000L * 60L, true);
                if (minutes == - 1) {
                    sendFeedback(ban, new TranslatableText("modmdo.banned.convert.indefinite", certificate.name, ((TemporaryCertificate) SharedVariables.banned.get(name)).formatRemaining()));
                } else {
                    sendFeedback(ban, new TranslatableText("modmdo.banned.overtime", certificate.name, new TemporaryCertificate(null, TimeUtil.millions(), minutes * 1000L * 60L).formatRemaining(), ((TemporaryCertificate) SharedVariables.banned.get(name)).formatRemaining()));
                }
            } else {
                sendFeedback(ban, new TranslatableText("modmdo.banned.time-limit", certificate.name, temp.formatRemaining()));
            }
        } else {
            if (already) {
                sendFeedback(ban, new TranslatableText("modmdo.banned.already.indefinite", certificate.name));
            } else {
                sendFeedback(ban, new TranslatableText("modmdo.banned.indefinite", certificate.name));
            }
        }
        saveVariables();
        SharedVariables.updateTemporaryBanNames(getServer(ban), true);
        return 0;
    }

    public void temporaryBan(String player, Certificate certificate, long millions, boolean add) {
        Certificate c;
        if (add) {
            if (millions == - 1) {
                if (banned.get(player) instanceof TemporaryCertificate) {
                    c = new PermanentCertificate(certificate.name, certificate.getIdentifier(), certificate.getRecorde().uuid());
                    banned.remove(certificate.name);
                    banned.put(certificate.name, c);
                    return;
                }
            } else {
                if (banned.get(player) instanceof TemporaryCertificate temp) {
                    temp.setMillions(temp.getMillions() + millions);
                    return;
                }
            }
        }
        if (millions == - 1) {
            c = new PermanentCertificate(certificate.name, certificate.getIdentifier(), certificate.getRecorde().uuid());
        } else {
            if (certificate.getIdentifier().equals("")) {
                c = new TemporaryCertificate(certificate.name, new LoginRecorde(certificate.name, certificate.getRecorde().uuid(), LoginRecordeType.TEMPORARY), TimeUtil.millions(), millions);
            } else {
                c = new TemporaryCertificate(certificate.name, new LoginRecorde(certificate.getRecorde().modmdoUniqueId(), null, LoginRecordeType.TEMPORARY), TimeUtil.millions(), millions);
            }
        }
        if (loginUsers.hasUser(player)) {
            c.setLastLanguage(SharedVariables.loginUsers.getUser(player).getLanguage().getName());
        }
        banned.put(certificate.name, c);
    }
}
