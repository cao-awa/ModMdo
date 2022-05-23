package com.github.zhuaidadaya.modmdo.commands.argument.whitelist;

import com.github.zhuaidadaya.modmdo.whitelist.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.command.*;
import net.minecraft.server.command.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;

public class ModMdoTemporaryWhitelistArgumentType implements ArgumentType<String> {
    public static ModMdoTemporaryWhitelistArgumentType whitelist() {
        return new ModMdoTemporaryWhitelistArgumentType();
    }

    public static TemporaryWhitelist getWhiteList(CommandContext<ServerCommandSource> context, String name) {
        String string = context.getArgument(name, String.class);
        TemporaryWhitelist whiteList = temporaryWhitelist.get(string);
        return whiteList == null ? new TemporaryWhitelist(string, -1, -1) : whiteList;
    }

    @Override
    public String parse(StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(temporaryWhitelist.keySet(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
}
