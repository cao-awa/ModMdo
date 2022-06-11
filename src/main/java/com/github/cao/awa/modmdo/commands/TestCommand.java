package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.hyacinth.logging.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.translate.*;

import static net.minecraft.server.command.CommandManager.*;

public class TestCommand extends SimpleCommand {
    @Override
    public TestCommand register() {
        SharedVariables.commandRegister.register(literal("testmodmdo").executes(e -> {
            SharedVariables.testing = ! SharedVariables.testing;
            SimpleCommandOperation.sendFeedback(e, TextUtil.translatable("testing: " + SharedVariables.testing));
            return 0;
        }).then(literal("trace").executes(e -> {
            PrintUtil.debugging = !PrintUtil.debugging;
            SimpleCommandOperation.sendFeedback(e, TextUtil.translatable("trace: " + PrintUtil.debugging));
            return 0;
        })));
        return this;
    }
}
