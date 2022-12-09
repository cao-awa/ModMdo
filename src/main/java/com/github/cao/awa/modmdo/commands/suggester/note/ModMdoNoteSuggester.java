package com.github.cao.awa.modmdo.commands.suggester.note;

import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;
import org.json.*;

import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public class ModMdoNoteSuggester {
    public static String getNote(String name) {
        return notes.getString(name);
    }

    public static <S> CompletableFuture<Suggestions> suggestionsArbitrary(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> keys = new ObjectArrayList<>();
        notes.keySet()
             .forEach(key -> {
                 Object value = notes.get(key);
                 if (value instanceof JSONObject json) {
                     json.keySet()
                         .forEach(key2 -> keys.add("\"" + key + "/" + key2 + "\""));
                 } else {
                     keys.add("\"" + key + "\"");
                 }
             });
        return CommandSource.suggestMatching(
                keys,
                builder
        );
    }

    public static <S> CompletableFuture<Suggestions> suggestionsCollected(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> keys = new ObjectArrayList<>();
        notes.keySet()
             .forEach(key -> {
                 Object value = notes.get(key);
                 if (value instanceof JSONObject) {
                     keys.add("\"" + key + "\"");
                 }
             });
        return CommandSource.suggestMatching(
                keys,
                builder
        );
    }

    public static <S> CompletableFuture<Suggestions> suggestionsCollector(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> keys = new ObjectArrayList<>();
        notes.keySet()
             .forEach(key -> {
                 Object value = notes.get(key);
                 if (value instanceof JSONObject) {
                     keys.add("\"" + key + "\"");
                 }
             });
        return CommandSource.suggestMatching(
                keys,
                builder
        );
    }
}
