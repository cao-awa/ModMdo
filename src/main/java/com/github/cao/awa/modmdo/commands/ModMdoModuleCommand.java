package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.commands.suggester.*;
import com.github.cao.awa.modmdo.module.*;
import com.github.cao.awa.modmdo.module.error.type.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.tree.*;
import net.minecraft.server.command.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class ModMdoModuleCommand extends SimpleCommand {
    private final CommandNode<ServerCommandSource> builder = literal("module").then(argument("path", StringArgumentType.string()).suggests(ModMdoModuleCommandSuggester::suggestions).then(literal("unload").executes(unload -> {
        String path = StringArgumentType.getString(unload, "path");
        commandRegister.unregister(ModMdoModuleCommandSuggester.getCommand(path), ModMdoModuleCommandSuggester.getModule(path));
        sendFeedback(unload, TextUtil.translatable("Unloaded: " + path));
        return 0;
    })).then(literal("load").executes(load -> {
        String path = StringArgumentType.getString(load, "path");
        if (ModMdoModuleCommandSuggester.getModule(path) instanceof ModMdoCommandModule<?> commandModule) {
            ModMdoModuleLoadStatusType code = commandModule.loadCommand(path);
            switch (code) {
                case NEED_PARENT -> {
                    sendFeedback(load, TextUtil.translatable("Failed because you need enable parent module: " + commandModule.getMainer()));
                }
                case LOADED -> {
                    sendFeedback(load, TextUtil.translatable("Loaded: " + path));
                }
                case EXCEPTION -> {
                    sendFeedback(load, TextUtil.translatable("Failed because has error(" + path + ")"));
                }
            }
        }
        return 0;
    }))).build();

    @Override
    public SimpleCommand register() {
        commandRegister.register(this, null);
        return this;
    }

    @Override
    public void unregister() {

    }

    @Override
    public String path() {
        return null;
    }

    @Override
    public CommandNode<ServerCommandSource> builder() {
        return builder;
    }

    @Override
    public String level() {
        return "module";
    }
}
