package com.github.zhuaidadaya.modmdo.commands;

import com.mojang.brigadier.builder.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;

public abstract class ConfigurableCommand<T extends ConfigurableCommand<?>> extends SimpleCommand {
    abstract T init();
    protected abstract T register();
}
