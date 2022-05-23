package com.github.zhuaidadaya.modmdo.commands.argument.extra;

import com.github.zhuaidadaya.modmdo.extra.loader.*;
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

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

public class ModMdoExtraArgumentType implements ArgumentType<UUID> {
    public static ModMdoExtraArgumentType extra() {
        return new ModMdoExtraArgumentType();
    }

    public static ModMdoExtra<?> getExtra(CommandContext<ServerCommandSource> context, String name) {
        UUID id = context.getArgument(name, UUID.class);
        return extras.getExtra(id);
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
