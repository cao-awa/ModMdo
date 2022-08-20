package com.github.cao.awa.modmdo.commands.switcher;

import com.mojang.brigadier.*;
import com.mojang.brigadier.builder.*;
import net.minecraft.server.command.*;

public class SwitcherCommandBuilder {
    public static ArgumentBuilder<ServerCommandSource, ?> switcher( Command<ServerCommandSource> enable, Command<ServerCommandSource> disable) {
        return CommandManager.literal("enable").executes(enable).then(CommandManager.literal("disable").executes(disable));
    }
}
