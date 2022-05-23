package com.github.zhuaidadaya.modmdo.commands;

import com.github.cao.awa.hyacinth.logging.*;
import net.minecraft.text.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class TestCommand extends SimpleCommand {
    @Override
    public TestCommand register() {
        commandRegister.register(literal("testmodmdo").executes(e -> {
            testing = ! testing;
            sendFeedback(e, new TranslatableText("testing: " + testing));
            return 0;
        }).then(literal("trace").executes(e -> {
            PrintUtil.debugging = !PrintUtil.debugging;
            sendFeedback(e, new TranslatableText("trace: " + PrintUtil.debugging));
            return 0;
        })));
        return this;
    }
}
