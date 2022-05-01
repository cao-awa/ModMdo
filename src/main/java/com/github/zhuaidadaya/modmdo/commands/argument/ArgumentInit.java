package com.github.zhuaidadaya.modmdo.commands.argument;

import net.minecraft.command.argument.*;
import net.minecraft.command.argument.serialize.*;

public class ArgumentInit {
    public static void init() {
        ArgumentTypes.register("modmdo:whitelist", ModMdoWhitelistArgumentType.class, new ConstantArgumentSerializer<>(ModMdoWhitelistArgumentType::whitelist));
        ArgumentTypes.register("modmdo:temporary_whitelist", ModMdoTemporaryWhitelistArgumentType.class, new ConstantArgumentSerializer<>(ModMdoTemporaryWhitelistArgumentType::whitelist));
    }
}
