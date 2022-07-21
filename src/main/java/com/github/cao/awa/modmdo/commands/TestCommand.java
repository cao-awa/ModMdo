package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.mojang.brigadier.tree.*;
import net.minecraft.server.command.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.commandRegister;
import static com.github.cao.awa.modmdo.storage.SharedVariables.delayTasks;
import static net.minecraft.server.command.CommandManager.literal;

public class TestCommand extends SimpleCommand {
    private final CommandNode<ServerCommandSource> builder = literal("testmodmdo").then(literal("debug").executes(e -> {
        SharedVariables.debug = ! SharedVariables.debug;
        SimpleCommandOperation.sendFeedback(e, TextUtil.translatable("debug: " + SharedVariables.debug));
        return 0;
    })).then(literal("develop").executes(e -> {
        delayTasks.submit(() -> {
            SharedVariables.testing = ! SharedVariables.testing;
            SimpleCommandOperation.sendFeedback(e, TextUtil.translatable("develop: " + SharedVariables.testing));
        }, 2);
        return 0;
    })).then(literal("shulker").executes(shulker -> {
        delayTasks.submit(() -> {
            SharedVariables.testingShulker = ! SharedVariables.testingShulker;
            SimpleCommandOperation.sendFeedback(shulker, TextUtil.translatable("shulker: " + SharedVariables.testingShulker));
        }, 2);
        return 0;
    }).then(literal("parallel").executes(parallel -> {
        delayTasks.submit(() -> {
            SharedVariables.testingParallel = ! SharedVariables.testingParallel;
            SimpleCommandOperation.sendFeedback(parallel, TextUtil.translatable("parallel: " + SharedVariables.testingParallel));
        }, 2);
        return 0;
    })).then(literal("unload").executes(unload -> {
        commandRegister.getCommand("temporary").unregister();
        return 0;
    })).then(literal("load").executes(load -> {
        commandRegister.getCommand("temporary").register();
        return 0;
    }))).build();

    @Override
    public TestCommand register() {
        commandRegister.register(this, null);
        return this;
    }

    @Override
    public void unregister() {

    }

    @Override
    public String path() {
        return "testmodmdo/test";
    }

    @Override
    public CommandNode<ServerCommandSource> builder() {
        return builder;
    }

    @Override
    public String level() {
        return "testmodmdo";
    }
}
