package com.github.cao.awa.modmdo.commands.argument.whitelist;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;
import net.minecraft.server.command.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoInviteArgumentType implements ArgumentType<String> {
    public static ModMdoInviteArgumentType invite() {
        return new ModMdoInviteArgumentType();
    }

    public static TemporaryCertificate getInvite(CommandContext<ServerCommandSource> context, String name) {
        String string = context.getArgument(name, String.class);
        TemporaryCertificate invite = temporaryInvite.get(string);
        if (invite == null) {
            TemporaryCertificate certificate = temporaryStation.get(string);
            if (certificate.getType().equals("invite")) {
                invite = certificate;
            }
        }
        return invite == null ? new TemporaryCertificate(string, - 1, - 1) : invite;
    }

    @Override
    public String parse(StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> list = EntrustParser.operation(new ObjectArrayList<>(), l -> {
            l.addAll(temporaryInvite.keySet());
            temporaryStation.values().forEach(certificate -> {
                if (certificate.getType().equals("invite")) {
                    l.add(certificate.getName());
                }
            });
        });
        return CommandSource.suggestMatching(list, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
}
