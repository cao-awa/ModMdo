package com.github.zhuaidadaya.modmdo.commands;

import com.github.cao.awa.hyacinth.logging.*;
import com.github.zhuaidadaya.modmdo.math.*;
import net.minecraft.block.*;
import net.minecraft.command.argument.*;
import net.minecraft.server.network.*;
import net.minecraft.server.world.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.explosion.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.*;
import static net.minecraft.world.World.*;

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
