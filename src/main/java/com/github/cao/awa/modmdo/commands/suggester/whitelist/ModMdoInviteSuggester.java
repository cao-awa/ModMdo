package com.github.cao.awa.modmdo.commands.suggester.whitelist;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;

import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoInviteSuggester {
    public static TemporaryCertificate getInvite(String name) {
        TemporaryCertificate invite = temporaryInvite.get(name);
        if (invite == null) {
            TemporaryCertificate certificate = temporaryStation.get(name);
            if ("invite".equals(certificate.getType())) {
                invite = certificate;
            }
        }
        return invite == null ? new TemporaryCertificate(name, - 1, - 1) : invite;
    }

    public static <S> CompletableFuture<Suggestions> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
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
}
