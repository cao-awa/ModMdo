package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.commands.suggester.note.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;
import net.minecraft.server.command.*;
import org.json.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class NoteCommand extends SimpleCommand {
    @Override
    public SimpleCommand register() {
        commandRegister.register(literal("note").then(literal("add").then(argument("name", StringArgumentType.string()).then(argument("info", StringArgumentType.string()).executes(add -> {
            String name = StringArgumentType.getString(add, "name");
            saveVariables(() -> notes.put(name, StringArgumentType.getString(add, "info")));
            sendFeedback(add, TextUtil.translatable("command.note.added", name));
            return 0;
        }).then(literal("collect").then(argument("collect", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsCollector).executes(collect -> {
            String collectorName = StringArgumentType.getString(collect, "collect");
            String name = StringArgumentType.getString(collect, "name");

            if (notes.has(collectorName)) {
                if (notes.get(collectorName) instanceof JSONObject collector) {
                    collector.put(name, StringArgumentType.getString(collect, "info"));
                } else {
                    sendFeedback(collect, TextUtil.translatable("command.note.cannot.be.collect.because.already.is.arbitrary", collectorName));
                    return - 1;
                }
            } else {
                notes.put(collectorName, new JSONObject().put(name, StringArgumentType.getString(collect, "info")));
            }

            saveVariables();
            sendFeedback(collect, TextUtil.translatable("command.note.added", name));
            return 0;
        })))))).then(literal("get").then(literal("arbitrary").then(argument("name", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsArbitrary).executes(get -> {
            String all = StringArgumentType.getString(get, "name");
            String collector = all.contains("/") ? all.substring(0, all.indexOf("/")) : null;
            String name = collector == null ? all : all.substring(all.indexOf("/") + 1);
            feedback(get, collector, name);
            return 0;
        }))).then(literal("collected").then(argument("collector", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsCollected).then(argument("name", StringArgumentType.string()).suggests((source, builder) -> {
            String collector = StringArgumentType.getString(source, "collector");
            if (notes.has(collector)) {
                ObjectArrayList<String> list = new ObjectArrayList<>();
                notes.getJSONObject(collector).keySet().forEach(key -> {
                    list.add("\"" + key + "\"");
                });
                return CommandSource.suggestMatching(list, builder);
            }
            return CommandSource.suggestMatching(new ArrayList<>(), builder);
        }).executes(get -> {
            feedback(get, StringArgumentType.getString(get, "collector"), StringArgumentType.getString(get, "name"));
            return 0;
        }))))).then(literal("delete").then(literal("arbitrary").then(argument("name", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsArbitrary).executes(delete -> {
            String all = StringArgumentType.getString(delete, "name");
            String collector = all.contains("/") ? all.substring(0, all.indexOf("/")) : null;
            String name = collector == null ? all : all.substring(all.indexOf("/") + 1);
            delete(delete, collector, name, false);
            saveVariables();
            return 0;
        }))).then(literal("collected").then(argument("collector", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsCollected).then(argument("name", StringArgumentType.string()).suggests((source, builder) -> {
            String collector = StringArgumentType.getString(source, "collector");
            if (notes.has(collector)) {
                ObjectArrayList<String> list = new ObjectArrayList<>();
                notes.getJSONObject(collector).keySet().forEach(key -> {
                    list.add("\"" + key + "\"");
                });
                return CommandSource.suggestMatching(list, builder);
            }
            return CommandSource.suggestMatching(new ArrayList<>(), builder);
        }).executes(get -> {
            delete(get, StringArgumentType.getString(get, "collector"), StringArgumentType.getString(get, "name"), false);
            saveVariables();
            return 0;
        })))).then(literal("collector").then(argument("name", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsCollector).executes(get -> {
            delete(get, null, StringArgumentType.getString(get, "name"), true);
            saveVariables();
            return 0;
        })))));
        return this;
    }

    public void feedback(CommandContext<ServerCommandSource> source, String collector, String name) {
        EntrustEnvironment.trys(() -> {
            if (collector != null) {
                if (notes.has(collector)) {
                    JSONObject collected = notes.getJSONObject(collector);
                    if (collected.has(name)) {
                        String info = collected.getString(name);
                        sendFeedback(source, TextUtil.translatable("command.note.information", collector + "/" + name, info));
                    } else {
                        sendFeedback(source, TextUtil.translatable("command.note.not.found", collector + "/" + name));
                    }
                } else {
                    sendFeedback(source, TextUtil.translatable("command.note.not.found", collector + "/" + name));
                }
            } else {
                if (notes.has(name)) {
                    sendFeedback(source, TextUtil.translatable("command.note.information", name, notes.getString(name)));
                } else {
                    sendFeedback(source, TextUtil.translatable("command.note.not.found", name));
                }
            }
        });
    }

    public void delete(CommandContext<ServerCommandSource> source, String collector, String name, boolean deleteCollector) {
        EntrustEnvironment.trys(() -> {
            if (collector != null) {
                if (notes.has(collector)) {
                    if (deleteCollector) {
                        notes.remove(collector);
                        sendFeedback(source, TextUtil.translatable("command.note.deleted", collector));
                    } else {
                        JSONObject collected = notes.getJSONObject(collector);
                        if (collected.has(name)) {
                            collected.remove(name);
                            sendFeedback(source, TextUtil.translatable("command.note.deleted", collector + "/" + name));
                        } else {
                            sendFeedback(source, TextUtil.translatable("command.note.not.found", collector + "/" + name));
                        }
                    }
                } else {
                    sendFeedback(source, TextUtil.translatable("command.note.not.found", collector + "/" + name));
                }
            } else {
                if (notes.has(name)) {
                    notes.remove(name);
                    sendFeedback(source, TextUtil.translatable("command.note.deleted", name));
                } else {
                    sendFeedback(source, TextUtil.translatable("command.note.not.found", name));
                }
            }
        });
    }
}
