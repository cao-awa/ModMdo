package com.github.cao.awa.modmdo.commands.extra;

import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.storage.*;
import net.minecraft.text.*;

import static net.minecraft.server.command.CommandManager.*;

public class ExtraCommand extends SimpleCommand {
    @Override
    protected SimpleCommand register() {
        SharedVariables.commandRegister.register(literal("extra").then(literal("major").then(literal("enable").executes(major -> {
            SharedVariables.extras.get(SharedVariables.EXTRA_ID).active();
            SharedVariables.extras.force();
            sendFeedback(major, new TranslatableText("modmdo.extra.major.enabled"), 29);
            return 0;
        }))));
        return this;
    }
}
