package com.github.cao.awa.modmdo.commands.suggester.extra;

import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.storage.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;

import java.util.*;
import java.util.concurrent.*;

public class ModMdoExtraSuggester {
    public static ModMdoExtra<?> getExtra(String uuid) {
        return SharedVariables.extras.getExtra(UUID.fromString(uuid));
    }

    public static <S> CompletableFuture<Suggestions> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> ids = new ObjectArrayList<>();
        return CommandSource.suggestMatching(
                ids,
                builder
        );
    }
}
