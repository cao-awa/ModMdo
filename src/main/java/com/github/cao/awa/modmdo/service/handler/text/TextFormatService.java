package com.github.cao.awa.modmdo.service.handler.text;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.format.minecraft.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.resource.loader.*;
import com.github.cao.awa.modmdo.usr.*;

/**
 * Use service mode to handle text format.
 *
 * @author 草二号机
 * @since 1.0.43
 */
public class TextFormatService {
    private final MinecraftTextFormat minecraftTextFormat;

    public TextFormatService() {
        this.minecraftTextFormat = new MinecraftTextFormat();
    }

    public void attach(Resource<String> resource) {
        this.minecraftTextFormat.attach(resource);
    }

    public Literal format(String key, Object... args) {
        return this.minecraftTextFormat.format(key, args);
    }

    public Literal format(User user, String key, Object... args) {
        return this.minecraftTextFormat.format(user, key, args);
    }

    public Literal format(Language language, String key, Object... args) {
        return this.minecraftTextFormat.format(language, key, args);
    }

    public Literal format(Dictionary dictionary, String key, Object... args) {
        return this.minecraftTextFormat.format(dictionary, key, args);
    }

    public Literal format(Dictionary dictionary, Translatable translatable) {
        return this.minecraftTextFormat.format(dictionary, translatable);
    }
}
