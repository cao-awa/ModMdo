package com.github.cao.awa.modmdo.commands.suggester.whitelist;

import com.github.cao.awa.modmdo.certificate.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.command.*;

import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoWhitelistSuggester {
    public static Certificate getWhiteList(String name) {
        Certificate whiteList = whitelist.get(name);
        return whiteList == null ? new TemporaryCertificate(name, - 1, - 1) : whiteList;
    }

    public static  <S> CompletableFuture<Suggestions> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(whitelist.keySet(), builder);
    }
}
