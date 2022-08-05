package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.commands.suggester.note.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.*;
import net.minecraft.server.command.*;
import org.json.*;

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
            feedback(get, StringArgumentType.getString(get, "name"));
            return 0;
        }))).then(literal("collected").then(argument("name", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsCollected).executes(get -> {
            feedback(get, StringArgumentType.getString(get, "name"));
            return 0;
        })))).then(literal("delete").then(literal("arbitrary").then(argument("name", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsArbitrary).executes(delete -> {
            delete(delete, StringArgumentType.getString(delete, "name"), false);
            saveVariables();
            return 0;
        }))).then(literal("collected").then(argument("name", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsCollected).executes(get -> {
            delete(get, StringArgumentType.getString(get, "name"), false);
            saveVariables();
            return 0;
        }))).then(literal("collector").then(argument("name", StringArgumentType.string()).suggests(ModMdoNoteSuggester::suggestionsCollector).executes(get -> {
            delete(get, StringArgumentType.getString(get, "name"), true);
            saveVariables();
            return 0;
        })))));
        return this;
    }

    public void feedback(CommandContext<ServerCommandSource> source, String all) {
        EntrustExecution.tryTemporary(() -> {
            String collector = all.contains("/") ? all.substring(0, all.indexOf("/")) : null;
            String next = collector == null ? all : all.substring(all.indexOf("/") + 1);
            if (collector != null) {
                if (notes.has(collector)) {
                    JSONObject collected = notes.getJSONObject(collector);
                    if (collected.has(next)) {
                        String info = collected.getString(next);
                        sendFeedback(source, TextUtil.translatable("command.note.information", all, info));
                    } else {
                        sendFeedback(source, TextUtil.translatable("command.note.not.found", all));
                    }
                } else {
                    sendFeedback(source, TextUtil.translatable("command.note.not.found", all));
                }
            } else {
                if (notes.has(all)) {
                    sendFeedback(source, TextUtil.translatable("command.note.information", all, notes.getString(all)));
                } else {
                    sendFeedback(source, TextUtil.translatable("command.note.not.found", all));
                }
            }
        });
    }

    public void delete(CommandContext<ServerCommandSource> source, String all, boolean isCollect) {
        EntrustExecution.tryTemporary(() -> {
            String collector = all.contains("/") ? all.substring(0, all.indexOf("/")) : null;
            String next = collector == null ? all : all.substring(all.indexOf("/") + 1);
            if (collector != null) {
                System.out.println(collector);
                if (notes.has(collector)) {
                    if (isCollect) {
                        notes.remove(collector);
                        sendFeedback(source, TextUtil.translatable("command.note.deleted", collector));
                    } else {
                        JSONObject collected = notes.getJSONObject(collector);
                        if (collected.has(next)) {
                            collected.remove(next);
                            sendFeedback(source, TextUtil.translatable("command.note.deleted", all));
                        } else {
                            sendFeedback(source, TextUtil.translatable("command.note.not.found", all));
                        }
                    }
                } else {
                    sendFeedback(source, TextUtil.translatable("command.note.not.found", all));
                }
            } else {
                if (notes.has(next)) {
                    notes.remove(next);
                    sendFeedback(source, TextUtil.translatable("command.note.deleted", next));
                } else {
                    sendFeedback(source, TextUtil.translatable("command.note.not.found", next));
                }
            }
        });
    }
}
