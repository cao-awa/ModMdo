package com.github.cao.awa.modmdo.event.register;

import com.github.cao.awa.modmdo.extra.loader.*;

import java.io.*;

public class ModMdoEventRegister {
    private final ModMdoExtra<?> extra;
    private final String name;

    public ModMdoEventRegister(ModMdoExtra<?> extra, File file) {
        this.extra = extra;
        this.name = file.getName();
    }

    public ModMdoExtra<?> getExtra() {
        return extra;
    }

    public String getName() {
        return name;
    }

    public ModMdoEventRegister(ModMdoExtra<?> extra, String name) {
        this.extra = extra;
        this.name = name;
    }
}
