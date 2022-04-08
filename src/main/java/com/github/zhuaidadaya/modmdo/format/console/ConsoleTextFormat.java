package com.github.zhuaidadaya.modmdo.format.console;

import com.github.zhuaidadaya.modmdo.lang.Language;
import com.github.zhuaidadaya.modmdo.resourceLoader.Resource;
import com.github.zhuaidadaya.modmdo.format.TextFormat;

public class ConsoleTextFormat extends TextFormat<String> {
    public ConsoleTextFormat(Resource<Language> resource) {
        super(resource);
    }

    public String format(String key, Object... args) {
        return formatted(key, args);
    }
}