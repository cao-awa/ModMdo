package com.github.cao.awa.modmdo.commands.argument.connection;

import com.github.cao.awa.modmdo.network.forwarder.process.*;
import com.github.cao.awa.modmdo.storage.*;
import com.mojang.brigadier.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;
import net.minecraft.server.command.*;
import net.minecraft.util.*;

import java.util.*;
import java.util.concurrent.*;

public class ModMdoConnectionArgumentType implements ArgumentType<String> {
    public static ModMdoConnectionArgumentType connection() {
        return new ModMdoConnectionArgumentType();
    }

    public static Pair<String, ModMdoDataProcessor> getConnection(CommandContext<ServerCommandSource> context, String name) {
        String string = context.getArgument(name, String.class);
        for (ModMdoDataProcessor processor : SharedVariables.modmdoConnections) {
            if (processor.getModMdoConnection().getName().equals(string)) {
                return new Pair<>(string, processor);
            }
        }
        return new Pair<>(string, null);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> names = new ObjectArrayList<>();
        for (ModMdoDataProcessor processor : SharedVariables.modmdoConnections) {
            names.add("\"" + processor.getModMdoConnection().getName() + "\"");
        }
        return CommandSource.suggestMatching(names, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return ArgumentType.super.getExamples();
    }
}
