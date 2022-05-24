package com.github.cao.awa.modmdo.commands.argument.whitelist;

import com.github.cao.awa.modmdo.whitelist.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.command.*;
import net.minecraft.server.command.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoWhitelistArgumentType implements ArgumentType<String> {
    public static ModMdoWhitelistArgumentType whitelist() {
        return new ModMdoWhitelistArgumentType();
    }

    public static Whitelist getWhiteList(CommandContext<ServerCommandSource> context, String name) {
        String string = context.getArgument(name, String.class);
        Whitelist whiteList = whitelist.get(string);
        return whiteList == null ? new TemporaryWhitelist(string, - 1, - 1) : whiteList;
    }

    @Override
    public String parse(StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(whitelist.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
}
