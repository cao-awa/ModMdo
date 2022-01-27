package com.github.zhuaidadaya.modMdo.commands.init;

import com.github.zhuaidadaya.modMdo.commands.ProjectListArgument;
import com.github.zhuaidadaya.modMdo.commands.wrap.ServerWrapArgument;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class ArgumentInit {
    public static void initProject() {
        ArgumentTypes.register("modmdo:projects", ProjectListArgument.class, new ConstantArgumentSerializer<>(ProjectListArgument :: projectList));
    }

    public static void initServerWrap() {
        ArgumentTypes.register("modmdo:servers_wrap", ServerWrapArgument.class, new ConstantArgumentSerializer<>(ServerWrapArgument :: servers));
    }
}
