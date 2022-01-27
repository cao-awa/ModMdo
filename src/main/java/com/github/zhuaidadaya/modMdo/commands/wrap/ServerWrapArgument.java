package com.github.zhuaidadaya.modMdo.commands.wrap;

import com.github.zhuaidadaya.modMdo.wrap.server.ServerInformation;
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

import static com.github.zhuaidadaya.modMdo.storage.Variables.servers;

public class ServerWrapArgument implements ArgumentType<String> {
    public static final DynamicCommandExceptionType INVALID_NAME_EXCEPTION = new DynamicCommandExceptionType((name) -> {
        return new TranslatableText("argument.server.unknown", name);
    });
    private static final Collection<String> EXAMPLES = List.of("127.0.0.1");

    private ServerWrapArgument() {
    }

    public static ServerWrapArgument servers() {
        return new ServerWrapArgument();
    }

    public static ServerInformation getServer(CommandContext<ServerCommandSource> context, String name) {
        if(servers.hasServer(context.getArgument(name, String.class))) {
            return servers.getServer(context.getArgument(name, String.class));
        } else {
            ServerInformation information = new ServerInformation();
            information.setName(context.getArgument(name, String.class));
            information.setError(true);
            return information;
        }
    }

    @Override
    public String parse(StringReader stringReader) throws CommandSyntaxException {
        String string = stringReader.readUnquotedString();
        if(! servers.hasServer(string)) {
            throw INVALID_NAME_EXCEPTION.create(string);
        } else {
            return string;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(new ServerWrapListArgument().getServersName(), builder);
    }


    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
