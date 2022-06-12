package com.github.cao.awa.modmdo.commands.argument;

import com.github.cao.awa.modmdo.commands.argument.ban.*;
import com.github.cao.awa.modmdo.commands.argument.connection.*;
import com.github.cao.awa.modmdo.commands.argument.whitelist.*;
import net.minecraft.command.argument.*;
import net.minecraft.command.argument.serialize.*;

public class ArgumentInit {
    public static void init() {
        ArgumentTypes.register("modmdo:whitelist", ModMdoWhitelistArgumentType.class, new ConstantArgumentSerializer<>(ModMdoWhitelistArgumentType::whitelist));
        ArgumentTypes.register("modmdo:temporary_whitelist", ModMdoTemporaryWhitelistArgumentType.class, new ConstantArgumentSerializer<>(ModMdoTemporaryWhitelistArgumentType::whitelist));
        ArgumentTypes.register("modmdo:connections", ModMdoConnectionArgumentType.class, new ConstantArgumentSerializer<>(ModMdoConnectionArgumentType::connection));
        ArgumentTypes.register("modmdo:banned", ModMdoTemporaryBanArgumentType.class, new ConstantArgumentSerializer<>(ModMdoTemporaryBanArgumentType::banned));
        ArgumentTypes.register("modmdo:pass", ModMdoInviteArgumentType.class, new ConstantArgumentSerializer<>(ModMdoInviteArgumentType::pass));
    }
}
