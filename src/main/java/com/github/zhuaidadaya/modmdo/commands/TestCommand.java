package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.utils.command.*;
import com.mojang.brigadier.arguments.*;
import it.unimi.dsi.fastutil.objects.*;
import net.fabricmc.fabric.api.command.v1.*;
import net.minecraft.text.*;
import net.minecraft.util.math.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;
import static net.minecraft.server.command.CommandManager.*;
import static net.minecraft.world.World.*;

public class TestCommand extends SimpleCommandOperation implements SimpleCommand {
    @Override
    public void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("testmodmdo").executes(e -> {
                testing = ! testing;
                sendFeedback(e, new TranslatableText("testing: " + testing));
                return 0;
            }));
        });
    }
}
