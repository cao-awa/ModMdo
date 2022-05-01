package com.github.zhuaidadaya.modmdo.commands.argument;

import com.github.zhuaidadaya.modmdo.whitelist.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.*;
import net.minecraft.command.*;
import net.minecraft.server.command.*;
import net.minecraft.text.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ModMdoTemporaryWhitelistArgumentType implements ArgumentType<String> {
    private static final DynamicCommandExceptionType WHITELIST_NOT_FOUND = new DynamicCommandExceptionType((name) -> {
        return new TranslatableText("arguments.whitelist.not.registered", name);
    });

    public static ModMdoTemporaryWhitelistArgumentType whitelist() {
        return new ModMdoTemporaryWhitelistArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String string = reader.readUnquotedString();
        return string;
    }

    public static WhiteList getWhiteList(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        String string = context.getArgument(name, String.class);
        WhiteList whiteList = whitelist.get(string);
        if (whiteList == null) {
            throw WHITELIST_NOT_FOUND.create(string);
        } else {
            return whiteList;
        }
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
