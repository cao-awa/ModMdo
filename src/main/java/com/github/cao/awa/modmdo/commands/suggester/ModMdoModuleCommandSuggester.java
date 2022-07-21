package com.github.cao.awa.modmdo.commands.suggester;

import com.github.cao.awa.modmdo.commands.*;
import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.module.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.activity.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.brigadier.context.*;
import com.mojang.brigadier.suggestion.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.command.*;

import java.util.concurrent.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.commandRegister;
import static com.github.cao.awa.modmdo.storage.SharedVariables.extras;

public class ModMdoModuleCommandSuggester {
    public static SimpleCommand getCommand(String path) {
        return commandRegister.getCommand(path);
    }

    public static ModMdoModule<?> getModule(String path) {
        Receptacle<ModMdoModule<?>> result = new Receptacle<>(null);
        extras.getExtras().values().stream().filter(ActivityObject::isActive).forEach(activity -> {
            ModMdoExtra<?> extra = activity.get();
            extra.getModules().values().stream().forEach(module -> {
                if (result.get() != null) {
                    return;
                }
                if (module instanceof ModMdoCommandModule<?> commandModule) {
                    commandModule.getCommands().keySet().stream().filter(path::equals).forEach(m -> result.set(module));
                }
            });
        });
        return result.get();
    }

    public static <S> CompletableFuture<Suggestions> suggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        ObjectArrayList<String> list = new ObjectArrayList<>();
        extras.getExtras().values().stream().filter(ActivityObject::isActive).forEach(activity -> {
            ModMdoExtra<?> extra = activity.get();
            extra.getModules().values().forEach(module -> {
                if (module instanceof ModMdoCommandModule<?> commandModule) {
                    commandModule.getCommands().keySet().forEach(s -> {
                        list.add("\"" + s + "\"");
                    });
                }
            });
        });
        return CommandSource.suggestMatching(list, builder);
    }
}
