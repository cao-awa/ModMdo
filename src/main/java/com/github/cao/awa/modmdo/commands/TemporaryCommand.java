package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.certificate.pass.*;
import com.github.cao.awa.modmdo.commands.argument.ban.*;
import com.github.cao.awa.modmdo.commands.argument.whitelist.*;
import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.entity.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import net.minecraft.server.command.*;
import net.minecraft.server.network.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class TemporaryCommand extends SimpleCommand {
    @Override
    public TemporaryCommand register() {
        commandRegister.register(literal("temporary").requires(e -> e.hasPermissionLevel(4)).then(literal("ban").then(literal("add").then(argument("target", ModMdoWhitelistArgumentType.whitelist()).then(argument("minutes", IntegerArgumentType.integer(1)).executes(ban -> {
            return ban(ban, ModMdoWhitelistArgumentType.getWhiteList(ban, "target").name, IntegerArgumentType.getInteger(ban, "minutes"));
        })).then(literal("-1").executes(ban -> {
            return ban(ban, ModMdoWhitelistArgumentType.getWhiteList(ban, "target").name, - 1);
        })).then(literal("60").executes(ban -> {
            return ban(ban, ModMdoWhitelistArgumentType.getWhiteList(ban, "target").name, 60);
        })))).then(literal("remove").then(argument("target", ModMdoTemporaryBanArgumentType.banned()).executes(remove -> {
            Certificate certificate = ModMdoTemporaryBanArgumentType.getCertificate(remove, "target");
            if (banned.containsName(certificate.getName())) {
                banned.remove(certificate.getName());
                saveVariables();
                sendFeedback(getPlayer(remove), TextUtil.translatable("modmdo.ban.pardon", certificate.getName()));
                return 0;
            }
            sendFeedback(getPlayer(remove), TextUtil.translatable("arguments.banned.not.registered", certificate.getName()));
            return - 1;
        }))).then(literal("list").executes(list -> {
            showTemporaryBan(list);
            updateTemporaryBanNames(getServer(list), true);
            return 0;
        })).then(literal("reduce").then(argument("target", ModMdoTemporaryBanArgumentType.banned()).then(argument("minutes", IntegerArgumentType.integer(1)).executes(ban -> {
            String name = ModMdoTemporaryBanArgumentType.getCertificate(ban, "target").getName();
            if (banned.containsName(name)) {
                int minute = IntegerArgumentType.getInteger(ban, "minutes");
                return ban(ban, name, - minute);
            } else {
                sendError(ban, TextUtil.translatable("arguments.banned.not.registered", name));
            }
            return - 1;
        }))))).then(literal("invite").then(literal("add").then(argument("target", StringArgumentType.string()).then(argument("minutes", IntegerArgumentType.integer(1)).executes(invite -> {
            String name = StringArgumentType.getString(invite, "target");
            int minute = IntegerArgumentType.getInteger(invite, "minutes");
            return invite(invite, name, minute);
        })).then(literal("60").executes(invite -> {
            String name = StringArgumentType.getString(invite, "target");
            return invite(invite, name, 60);
        })).then(literal("5").executes(invite -> {
            String name = StringArgumentType.getString(invite, "target");
            return invite(invite, name, 5);
        })))).then(literal("remove").then(argument("target", ModMdoInviteArgumentType.invite()).executes(remove -> {
            Certificate certificate = ModMdoInviteArgumentType.getInvite(remove, "target");
            Receptacle<Boolean> success = new Receptacle<>(false);
            EntrustExecution.notNull(temporaryStation.get(certificate.getName()), c -> {
                if (c.getType().equals("invite")) {
                    temporaryStation.remove(c.getName());
                    success.set(true);
                }
            });
            EntrustExecution.notNull(temporaryInvite.get(certificate.getName()), invite -> {
                invite.setMillions(- 1);
                success.set(true);
            });
            if (success.get()) {
                sendFeedback(remove, TextUtil.translatable("modmdo.invite.cancel", certificate.getName()));
            } else {
                sendError(remove, TextUtil.translatable("arguments.invite.not.registered", EntityUtil.getName(getPlayer(remove))));
            }
            return 0;
        }))).then(literal("list").executes(list -> {
            showTemporaryInvite(list);
            updateTemporaryInviteNames(getServer(list), true);
            return 0;
        })).then(literal("reduce").then(argument("target", ModMdoInviteArgumentType.invite()).then(argument("minutes", IntegerArgumentType.integer(1)).executes(invite -> {
            String name = ModMdoInviteArgumentType.getInvite(invite, "target").getName();
            if (temporaryInvite.containsName(name)) {
                int minute = IntegerArgumentType.getInteger(invite, "minutes");
                return invite(invite, name, - minute);
            } else {
                sendError(invite, TextUtil.translatable("arguments.invite.not.registered", name));
            }
            return - 1;
        }))))).then(literal("whitelist").then(literal("add").then(argument("name", StringArgumentType.string()).executes(addDefault -> {
            whitelist(addDefault);
            return 0;
        }))).then(literal("list").executes(showTemporary -> {
            showTemporaryWhitelist(showTemporary);
            updateTemporaryWhitelistNames(getServer(showTemporary), true);
            return 0;
        })).then(literal("remove").then(argument("name", ModMdoTemporaryWhitelistArgumentType.whitelist()).executes(remove -> {
            TemporaryCertificate wl = ModMdoTemporaryWhitelistArgumentType.getWhiteList(remove, "name");
            if (temporaryStation.containsName(wl.getName())) {
                temporaryStation.remove(wl.getName());
                sendFeedback(remove, TextUtil.translatable("temporary.station.removed", wl.getName()));
                updateTemporaryWhitelistNames(getServer(remove), true);
                return 0;
            }
            sendError(remove, TextUtil.translatable("arguments.temporary.station.not.registered", wl.getName()));
            return - 1;
        })))).then(literal("connection").then(literal("whitelist").executes(whitelist -> {
            if (modmdoConnectionAccepting.isValid()) {
                long million = modmdoConnectionAccepting.getMillions() - TimeUtil.processMillion(modmdoConnectionAccepting.getRecording());
                long minute = TimeUtil.processRemainingMinutes(million);
                long second = TimeUtil.processRemainingSeconds(million);
                sendFeedback(whitelist, TextUtil.translatable("connection.whitelist.accepting", minute, second));
            } else {
                sendFeedback(whitelist, TextUtil.translatable("connection.whitelist.no.accepting"));
            }
            return 0;
        }).then(literal("accept").then(literal("one").executes(acceptOne -> {
            if (modmdoConnectionAccepting.isValid()) {
                long million = modmdoConnectionAccepting.getMillions() - TimeUtil.processMillion(modmdoConnectionAccepting.getRecording());
                long minute = TimeUtil.processRemainingMinutes(million);
                long second = TimeUtil.processRemainingSeconds(million);
                sendError(acceptOne, TextUtil.translatable("connection.whitelist.accepting", minute, second));
            } else {
                sendFeedback(acceptOne, TextUtil.translatable("connection.whitelist.accepting.one"));
                modmdoConnectionAccepting = new TemporaryCertificate("", TimeUtil.millions(), 1000 * 60 * 5);
            }
            return 0;
        }))).then(literal("cancel").executes(cancel -> {
            if (modmdoConnectionAccepting.isValid()) {
                modmdoConnectionAccepting = new TemporaryCertificate("", - 1, - 1);
            } else {
                sendError(cancel, TextUtil.translatable("connection.whitelist.no.accepting"));
            }
            return 0;
        })))));
        return this;
    }

    public void showTemporaryWhitelist(CommandContext<ServerCommandSource> source) throws CommandSyntaxException {
        SharedVariables.flushTemporaryWhitelist();
        ServerPlayerEntity player = getPlayer(source);
        if (SharedVariables.temporaryStation.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (TemporaryCertificate wl : SharedVariables.temporaryStation.values()) {
                builder.append(wl.getName()).append(": ");
                builder.append(wl.formatRemaining());
                builder.append("\n");
            }
            builder.delete(builder.length() - 1, builder.length());
            sendMessage(player, TextUtil.translatable("commands.temporary.station.list", SharedVariables.temporaryStation.size(), builder.toString()), false);
        } else {
            sendMessage(player, TextUtil.translatable("commands.temporary.station.none"), false);
        }
    }

    public int whitelist(CommandContext<ServerCommandSource> source) {
        String name = StringArgumentType.getString(source, "name");
        if (SharedVariables.temporaryStation.containsName(name)) {
            sendFeedback(source, TextUtil.translatable("temporary.station.add.already.is", name));
            return - 1;
        }
        if (SharedVariables.whitelist.containsName(name)) {
            sendFeedback(source, TextUtil.translatable("modmdo.whitelist.add.already.is", name));
            return - 1;
        }
        temporaryWhitelist(name, 1000 * 60 * 5);
        sendFeedback(source, TextUtil.translatable("temporary.station.add.default", name));
        SharedVariables.updateTemporaryWhitelistNames(getServer(source), true);
        return 0;
    }

    public void temporaryWhitelist(String name, long millions) {
        SharedVariables.temporaryStation.put(name, new TemporaryCertificate(name, TimeUtil.millions(), millions).setType("whitelist"));
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
                sendMessage(player, TextUtil.translatable("commands.temporary.ban.list", banned.size(), builder.toString()), false);
            } else {
                sendMessage(player, TextUtil.translatable("commands.temporary.ban.none"), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    sendFeedback(ban, TextUtil.translatable("modmdo.banned.convert.indefinite", certificate.name, ((TemporaryCertificate) SharedVariables.banned.get(name)).formatRemaining()));
                } else {
                    if (temp.getMillions() > 0) {
                        sendFeedback(ban, TextUtil.translatable(minutes > 0 ? "modmdo.banned.overtime" : "modmdo.banned.reduce", name, new TemporaryCertificate(null, TimeUtil.millions(), minutes * 1000L * 60L).formatRemaining(), ((TemporaryCertificate) SharedVariables.banned.get(name)).formatRemaining()));
                    } else {
                        SharedVariables.banned.remove(temp.getName());
                        sendFeedback(ban, TextUtil.translatable("modmdo.ban.pardon", name));
                    }
                }
            } else {
                sendFeedback(ban, TextUtil.translatable("modmdo.banned.time-limit", certificate.name, temp.formatRemaining()));
            }
        } else {
            if (already) {
                sendFeedback(ban, TextUtil.translatable("modmdo.banned.already.indefinite", certificate.name));
            } else {
                sendFeedback(ban, TextUtil.translatable("modmdo.banned.indefinite", certificate.name));
            }
        }
        saveVariables();
        SharedVariables.updateTemporaryBanNames(getServer(ban), true);
        return 0;
    }

    public void temporaryBan(String player, Certificate certificate, long millions, boolean add) {
        Certificate c;
        if (add) {
            if (banned.get(player) instanceof TemporaryCertificate temp) {
                temp.setMillions(temp.getMillions() + millions);
                return;
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
        c.setType("ban");
        banned.put(certificate.name, c);
    }

    public int invite(CommandContext<ServerCommandSource> invite, String name, int minutes) {
        try {
            if (SharedVariables.temporaryStation.containsName(name)) {
                sendFeedback(invite, TextUtil.translatable("temporary.station.add.already.is", name));
                return - 1;
            }
            if (SharedVariables.whitelist.containsName(name)) {
                sendFeedback(invite, TextUtil.translatable("modmdo.invite.add.already.is", name));
                return - 1;
            }
            TemporaryCertificate invited = temporaryInvite.get(name);
            boolean already = false;
            if (invited == null) {
                temporaryInvite(EntityUtil.getName(getPlayer(invite)), name, minutes == - 1 ? - 1 : minutes * 1000L * 60L, false);
            } else {
                already = true;
            }
            if (already) {
                temporaryInvite(EntityUtil.getName(getPlayer(invite)), name, minutes == - 1 ? - 1 : minutes * 1000L * 60L, true);
                if (invited.getMillions() > 0) {
                    sendFeedback(invite, TextUtil.translatable(minutes > 0 ? "modmdo.invite.overtime" : "modmdo.invite.reduce", name, new TemporaryCertificate(null, TimeUtil.millions(), minutes * 1000L * 60L).formatRemaining(), temporaryInvite.get(name).formatRemaining()));
                } else {
                    temporaryInvite(EntityUtil.getName(getPlayer(invite)), name, - 1, false);
                    sendFeedback(invite, TextUtil.translatable("modmdo.invite.cancel", name));
                }
            } else {
                sendFeedback(invite, TextUtil.translatable("temporary.station.add.default", name));
            }
            SharedVariables.updateTemporaryInviteNames(getServer(invite), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void temporaryInvite(String organizer, String name, long millions, boolean add) {
        TemporaryCertificate certificate;
        if (add) {
            TemporaryCertificate temp = temporaryInvite.get(name);
            temp.setMillions(temp.getMillions() + millions);
            return;
        }
        if (temporaryInvite.containsName(name)) {
            certificate = temporaryInvite.get(name);
            certificate.setMillions(millions);
        } else {
            certificate = new TemporaryCertificate(name, new LoginRecorde(name, null, LoginRecordeType.TEMPORARY), TimeUtil.millions(), 1000L * 5 * 60);
            certificate.setType("invite");
            certificate.setSpare(new TemporaryCertificate(name, - 1, millions));
            certificate.setPass(new TemporaryPass().setTime(millions).setOrganizer(organizer));
            temporaryStation.put(name, certificate);
        }
    }

    public void showTemporaryInvite(CommandContext<ServerCommandSource> source) {
        try {
            SharedVariables.flushTemporaryBan();
            ServerPlayerEntity player = getPlayer(source);
            int count = 0;
            StringBuilder builder = new StringBuilder();
            for (TemporaryCertificate certificate : temporaryInvite.values()) {
                count++;
                builder.append(certificate.getName()).append(": ");
                builder.append(minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), "commands.temporary.invite.remaining", certificate.formatRemaining()).getString());
                builder.append("\n");
            }
            for (TemporaryCertificate certificate : temporaryStation.values()) {
                if (certificate.getType().equals("invite")) {
                    count++;
                    builder.append(certificate.getName()).append(": ");
                    builder.append(minecraftTextFormat.format(loginUsers.getUser(getPlayer(source)), "commands.temporary.invite.not.ready", certificate.formatRemaining()).getString());
                    builder.append("\n");
                }
            }
            if (count > 0) {
                builder.delete(builder.length() - 1, builder.length());
                sendMessage(player, TextUtil.translatable("commands.temporary.invite.list", temporaryInvite.size(), builder.toString()), false);
            } else {
                sendMessage(player, TextUtil.translatable("commands.temporary.invite.none"), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
