package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.utils.command.SimpleCommandOperation;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.text.TranslatableText;

import static com.github.zhuaidadaya.modmdo.storage.Variables.testing;
import static net.minecraft.server.command.CommandManager.literal;

public class TestCommand extends SimpleCommandOperation implements SimpleCommand {
    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("testmodmdo").executes(e -> {
                testing = !testing;
                sendFeedback(e, new TranslatableText("testing: " + testing));
                return 0;
            }));
        });
    }
}
