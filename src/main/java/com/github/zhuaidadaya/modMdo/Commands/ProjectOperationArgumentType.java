package com.github.zhuaidadaya.modMdo.Commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class ProjectOperationArgumentType implements ArgumentType<Integer> {
    private static final Collection<String> EXAMPLES = Arrays.asList("sidebar", "foo.bar");
    public static final DynamicCommandExceptionType INVALID_SLOT_EXCEPTION = new DynamicCommandExceptionType((name) -> {
        return new TranslatableText("argument.projectOperation.invalid", new Object[]{name});
    });

    private ProjectOperationArgumentType() {
    }

    public static ProjectOperationArgumentType projectOperationTypes() {
        return new ProjectOperationArgumentType();
    }

    public static int getScoreboardSlot(CommandContext<ServerCommandSource> context, String name) {
        return (Integer)context.getArgument(name, Integer.class);
    }

    public Integer parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        int i = ProjectArgument.getOperationId(string);
        if (i == -1) {
            throw INVALID_SLOT_EXCEPTION.create(string);
        } else {
            return i;
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(ProjectArgument.getDisplayOperationNames(), builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
