package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.projects.Project;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProjectListArgument implements ArgumentType<String> {
    public static final DynamicCommandExceptionType INVALID_NAME_EXCEPTION = new DynamicCommandExceptionType((name) -> {
        return new TranslatableText("argument.projectName.invalid", name);
    });
    private static final Collection<String> EXAMPLES = List.of("a", "b");

    private ProjectListArgument() {
    }

    public static ProjectListArgument projectList() {
        return new ProjectListArgument();
    }

    public static Project getProject(CommandContext<ServerCommandSource> context, String name) {
        return context.getArgument(name, Project.class);
    }

    @Override
    public String parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        int i = new ProjectArgument().getProjectId(string);
        if(i == - 1) {
            throw INVALID_NAME_EXCEPTION.create(string);
        } else {
            return string;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(new ProjectArgument().getProjectsName(), builder);
    }


    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
