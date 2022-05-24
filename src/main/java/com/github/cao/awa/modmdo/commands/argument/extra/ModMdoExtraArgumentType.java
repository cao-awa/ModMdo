package com.github.cao.awa.modmdo.commands.argument.extra;

import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.storage.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;
import net.minecraft.server.command.*;

import java.util.*;
import java.util.concurrent.*;

public class ModMdoExtraArgumentType implements ArgumentType<UUID> {
    public static ModMdoExtraArgumentType extra() {
        return new ModMdoExtraArgumentType();
    }

    public static ModMdoExtra<?> getExtra(CommandContext<ServerCommandSource> context, String name) {
        UUID id = context.getArgument(name, UUID.class);
        return SharedVariables.extras.getExtra(id);
    }

    @Override
    public UUID parse(StringReader reader) throws CommandSyntaxException {
        return UUID.fromString(reader.readString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> ids = new ObjectArrayList<>();
        return CommandSource.suggestMatching(ids, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
}
