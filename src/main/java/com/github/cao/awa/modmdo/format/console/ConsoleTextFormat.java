package com.github.cao.awa.modmdo.format.console;

import com.github.cao.awa.modmdo.lang.Language;
import com.github.cao.awa.modmdo.resourceLoader.Resource;
import com.github.cao.awa.modmdo.format.TextFormat;

public class ConsoleTextFormat extends TextFormat<String> {
    public ConsoleTextFormat(Resource<Language> resource) {
        super(resource);
    }

    public String format(String key, Object... args) {
        return formatted(key, args);
    }
}