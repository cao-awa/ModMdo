package com.github.zhuaidadaya.modMdo.commands.init;

import com.github.zhuaidadaya.modMdo.commands.ProjectListArgument;
import com.github.zhuaidadaya.modMdo.commands.jump.ServerJumpArgument;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class ArgumentInit {
    public static void initProject() {
        ArgumentTypes.register("modmdo:projects", ProjectListArgument.class, new ConstantArgumentSerializer<>(ProjectListArgument :: projectList));
    }

    public static void initServerJump() {
        ArgumentTypes.register("modmdo:servers_jump", ServerJumpArgument.class, new ConstantArgumentSerializer<>(ServerJumpArgument :: servers));
    }
}
