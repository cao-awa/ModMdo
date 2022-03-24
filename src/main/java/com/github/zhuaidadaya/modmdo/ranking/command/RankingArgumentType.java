package com.github.zhuaidadaya.modmdo.ranking.command;


import com.github.zhuaidadaya.modmdo.ranking.Rank;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import static com.github.zhuaidadaya.modmdo.storage.Variables.supportedRankingObjects;

public class RankingArgumentType implements ArgumentType<Rank> {
    private static final Collection<String> EXAMPLES = new ObjectArrayList<>();
    private static final DynamicCommandExceptionType INVALID_RANK_EXCEPTION = new DynamicCommandExceptionType((name) -> {
        return new TranslatableText("argument.ranks.invalid", name);
    });

    public Rank parse(StringReader stringReader) {
        return new Rank(stringReader.readUnquotedString(), "unknown", "unknown");
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ArrayList<String> list = new ArrayList<>();
        for (Rank r : supportedRankingObjects.values()) {
            list.add(r.getName());
        }
        return CommandSource.suggestMatching(list, builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static RankingArgumentType ranks() {
        return new RankingArgumentType();
    }

    public static Rank getRankArgument(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        Rank rank = supportedRankingObjects.get(context.getArgument(name, Rank.class).getName());
        if (rank == null) {
            throw INVALID_RANK_EXCEPTION.create(null);
        } else {
            return rank;
        }
    }
}
