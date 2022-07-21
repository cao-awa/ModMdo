package com.github.cao.awa.modmdo.extra.modmdo.module.whitelist;

import com.github.cao.awa.modmdo.extra.loader.*;
import com.github.cao.awa.modmdo.extra.modmdo.module.whitelist.commands.*;
import com.github.cao.awa.modmdo.module.*;
import com.github.cao.awa.modmdo.module.error.type.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.github.cao.awa.modmdo.module.error.type.ModMdoModuleLoadStatusType.*;
import static com.github.cao.awa.modmdo.storage.SharedVariables.commandRegister;

public class WhitelistModule<T> extends ModMdoCommandModule<T> {
    public WhitelistModule(ModMdoExtra<T> parent) {
        super(parent);
    }

    @Override
    public ModMdoModuleLoadStatusType loadCommand(@NotNull String path) {
        return EntrustParser.trying(() -> {
            if (path.equals(getMainer()) || getCommand(getMainer()).isLoaded()) {
                commandRegister.register(getCommand(path), this);
                return LOADED;
            }
            return NEED_PARENT;
        }, ex -> EXCEPTION);
    }

    @Override
    public String getName() {
        return "whitelist";
    }

    @Override
    public void load() {
        initCommands();
    }

    @Override
    public List<String> unload(String path) {
        ObjectArrayList<String> list = new ObjectArrayList<>();
        if (path.equals(getMainer())) {
            list.addAll(getCommands().keySet());
            return list;
        }
        return List.of(path);
    }

    public void initCommands() {
        loadMainer(new ModMdoWhitelistCommand(this));
        load(new TemporaryCommand(this));
    }

    @Override
    public String commandLevel(String path) {
        return getCommand(path).level();
    }
}
