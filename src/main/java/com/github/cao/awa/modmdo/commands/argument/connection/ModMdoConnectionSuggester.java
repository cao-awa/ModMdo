package com.github.cao.awa.modmdo.commands.argument.connection;

import com.github.cao.awa.modmdo.network.forwarder.process.*;
import com.github.cao.awa.modmdo.storage.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

import java.util.concurrent.*;

public class ModMdoConnectionSuggester {
    public static Pair<String, ModMdoDataProcessor> getConnection(String name) {
        for (ModMdoDataProcessor processor : SharedVariables.modmdoConnections) {
            if (processor.getModMdoConnection().getName().equals(name)) {
                return new Pair<>(name, processor);
            }
        }
        return new Pair<>(name, null);
    }

    public static <S> CompletableFuture<Suggestions> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> names = new ObjectArrayList<>();
        for (ModMdoDataProcessor processor : SharedVariables.modmdoConnections) {
            names.add("\"" + processor.getModMdoConnection().getName() + "\"");
        }
        return CommandSource.suggestMatching(names, builder);
    }
}
