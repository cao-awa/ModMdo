package com.github.cao.awa.modmdo.commands.argument;

import com.github.cao.awa.modmdo.commands.argument.ban.*;
import com.github.cao.awa.modmdo.commands.argument.connection.*;
import com.github.cao.awa.modmdo.commands.argument.whitelist.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.command.argument.*;
import net.minecraft.command.argument.serialize.*;
import net.minecraft.util.registry.*;

import java.lang.reflect.*;

public class ArgumentInit {
    public static void init() {
        EntrustExecution.tryTemporary(() -> force(ArgumentTypes.class.getDeclaredMethod("register", Registry.class, String.class, Class.class, ArgumentSerializer.class), "modmdo:whitelist", ModMdoWhitelistArgumentType.class, ConstantArgumentSerializer.of(ModMdoWhitelistArgumentType::whitelist)));
        EntrustExecution.tryTemporary(() -> force(ArgumentTypes.class.getDeclaredMethod("register", Registry.class, String.class, Class.class, ArgumentSerializer.class), "modmdo:temporary_whitelist", ModMdoTemporaryWhitelistArgumentType.class, ConstantArgumentSerializer.of(ModMdoTemporaryWhitelistArgumentType::whitelist)));
        EntrustExecution.tryTemporary(() -> force(ArgumentTypes.class.getDeclaredMethod("register", Registry.class, String.class, Class.class, ArgumentSerializer.class), "modmdo:connections", ModMdoConnectionArgumentType.class, ConstantArgumentSerializer.of(ModMdoConnectionArgumentType::connection)));
        EntrustExecution.tryTemporary(() -> force(ArgumentTypes.class.getDeclaredMethod("register", Registry.class, String.class, Class.class, ArgumentSerializer.class), "modmdo:banned", ModMdoTemporaryBanArgumentType.class, ConstantArgumentSerializer.of(ModMdoTemporaryBanArgumentType::banned)));
        EntrustExecution.tryTemporary(() -> force(ArgumentTypes.class.getDeclaredMethod("register", Registry.class, String.class, Class.class, ArgumentSerializer.class), "modmdo:invite", ModMdoInviteArgumentType.class, ConstantArgumentSerializer.of(ModMdoInviteArgumentType::invite)));
    }

    public static void force(Method method, String id, Class<?> clazz, ArgumentSerializer<?,?> serializer) throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        method.invoke(null, Registry.COMMAND_ARGUMENT_TYPE, id, clazz, serializer);
    }
}
