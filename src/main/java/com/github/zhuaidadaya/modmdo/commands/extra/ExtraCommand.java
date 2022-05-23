package com.github.zhuaidadaya.modmdo.commands.extra;

import com.github.zhuaidadaya.modmdo.commands.*;
import net.minecraft.text.*;

import static com.github.zhuaidadaya.modmdo.storage.SharedVariables.*;
import static net.minecraft.server.command.CommandManager.*;

public class ExtraCommand extends SimpleCommand {
    @Override
    protected SimpleCommand register() {
        commandRegister.register(literal("extra").then(literal("major").then(literal("enable").executes(major -> {
            extras.get(EXTRA_ID).active();
            extras.force();
            sendFeedback(major, new TranslatableText("modmdo.extra.major.enabled"), 29);
            return 0;
        }))));
        return this;
    }
}
