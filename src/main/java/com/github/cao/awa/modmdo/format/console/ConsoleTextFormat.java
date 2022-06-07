package com.github.cao.awa.modmdo.format.console;

import com.github.cao.awa.modmdo.format.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.resourceLoader.*;

public class ConsoleTextFormat extends TextFormat<String> {
    public ConsoleTextFormat(Resource<String> resource) {
        super(resource);
    }

    public String format(String key, Object... args) {
        return formatted(key, args);
    }

    public String format(Dictionary dictionary, String key, Object... args) {
        return formatted(dictionary, key, args);
    }
}