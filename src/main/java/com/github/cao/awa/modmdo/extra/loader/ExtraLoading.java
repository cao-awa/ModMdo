package com.github.cao.awa.modmdo.extra.loader;

import com.github.cao.awa.modmdo.extra.modmdo.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import it.unimi.dsi.fastutil.objects.*;

public class ExtraLoading {
    private final ModMdoExtra<ModMdo> major;
    private final ObjectArrayList<ModMdoExtra<?>> then = new ObjectArrayList<>();

    public ExtraLoading(ModMdoExtra<ModMdo> major) {
        this.major = major;
    }

    public void then(ModMdoExtra<?> extra) {
        then.add(extra);
    }

    public void load(ExceptingConsumer<ModMdoExtra<?>> action) {
        EntrustExecution.tryTemporary(() -> action.accept(major));
        EntrustExecution.tryFor(then, action);
    }

    public void load(ExceptingConsumer<ModMdoExtra<?>> action, ExceptingConsumer<ModMdoExtra<?>> whenFailed) {
        EntrustExecution.ensureTrying(major, action, whenFailed);
        EntrustExecution.ensureTryFor(then, action, whenFailed);
    }
}
