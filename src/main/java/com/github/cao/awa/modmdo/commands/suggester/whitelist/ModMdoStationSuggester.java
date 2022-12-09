package com.github.cao.awa.modmdo.commands.suggester.whitelist;

import com.github.cao.awa.modmdo.security.certificate.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.command.*;

import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoStationSuggester {
    public static TemporaryCertificate getStation(String name) {
        TemporaryCertificate whiteList = temporaryStation.get(name);
        return whiteList == null ?
               new TemporaryCertificate(name,
                                        - 1,
                                        - 1
               ) :
               whiteList;
    }

    public static <S> CompletableFuture<Suggestions> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(
                temporaryStation.keySet(),
                builder
        );
    }
}
