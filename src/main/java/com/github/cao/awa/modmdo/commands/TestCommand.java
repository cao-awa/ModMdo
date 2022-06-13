package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.text.*;

import static net.minecraft.server.command.CommandManager.*;

public class TestCommand extends SimpleCommand {
    @Override
    public TestCommand register() {
        SharedVariables.commandRegister.register(literal("testmodmdo").then(literal("debug").executes(e -> {
            SharedVariables.debug = ! SharedVariables.debug;
            SimpleCommandOperation.sendFeedback(e, TextUtil.translatable("debug: " + SharedVariables.debug));
            return 0;
        })));
        return this;
    }
}
