package com.github.cao.awa.modmdo.commands.suggester.whitelist;

import com.github.cao.awa.modmdo.security.certificate.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;

import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoInviteSuggester {
    public static TemporaryCertificate getInvite(String name) {
        TemporaryCertificate invite = invitesService.get(name);
        if (invite == null) {
            TemporaryCertificate certificate = stationService.get(name);
            if ("invite".equals(certificate.getType())) {
                invite = certificate;
            } else {
                return new TemporaryCertificate(name,
                                         - 1,
                                         - 1
                );
            }
        }
        return invite;
    }

    public static <S> CompletableFuture<Suggestions> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> list = EntrustEnvironment.operation(
                new ObjectArrayList<>(),
                l -> {
                    l.addAll(invitesService.keys());
                    stationService.values()
                                  .forEach(certificate -> {
                                        if (certificate.getType()
                                                       .equals("invite")) {
                                            l.add(certificate.getName());
                                        }
                                    });
                }
        );
        return CommandSource.suggestMatching(
                list,
                builder
        );
    }
}
