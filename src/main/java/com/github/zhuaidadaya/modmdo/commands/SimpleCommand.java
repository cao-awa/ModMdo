package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.utils.command.*;
import com.mojang.brigadier.builder.*;
import net.minecraft.server.*;
import net.minecraft.server.command.*;

public abstract class SimpleCommand extends SimpleCommandOperation {
    protected abstract SimpleCommand register();
}
