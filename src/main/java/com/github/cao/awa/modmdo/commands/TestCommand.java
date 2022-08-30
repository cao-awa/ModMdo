package com.github.cao.awa.modmdo.commands;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.command.*;
import com.github.cao.awa.modmdo.utils.text.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.TEST_MODMDO_VERSION;
import static net.minecraft.server.command.CommandManager.*;

public class TestCommand extends SimpleCommand {
    @Override
    public TestCommand register() {
        SharedVariables.commandRegister.register(literal("testmodmdo").then(literal("debug").executes(e -> {
            SharedVariables.debug = ! SharedVariables.debug;
            SimpleCommandOperation.sendFeedback(e, TextUtil.translatable("debug: " + SharedVariables.debug));
            return 0;
        })).then(literal("develop").executes(e -> {
            SharedVariables.testing = !SharedVariables.testing;
            SimpleCommandOperation.sendFeedback(e, TextUtil.translatable("develop: " + SharedVariables.testing));
            return 0;
        })).then(literal("version").executes(version -> {
            sendFeedback(version, TextUtil.translatable(TEST_MODMDO_VERSION));
            return 0;
        })));
        return this;
    }
}
