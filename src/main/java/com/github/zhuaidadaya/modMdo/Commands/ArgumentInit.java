package com.github.zhuaidadaya.modMdo.Commands;

import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class ArgumentInit {
    public void init() {
        ArgumentTypes.register("modmdo:projects", ProjectListArgument.class,  new ConstantArgumentSerializer<>(ProjectListArgument::projectList));
    }
}
