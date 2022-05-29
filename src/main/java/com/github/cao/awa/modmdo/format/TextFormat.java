package com.github.cao.awa.modmdo.format;

import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.resourceLoader.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.text.*;
import org.json.*;

import java.util.*;

public abstract class TextFormat<T> {
    protected final Object2ObjectLinkedOpenHashMap<Language, Object2ObjectLinkedOpenHashMap<String, String>> format = new Object2ObjectLinkedOpenHashMap<>();

    public TextFormat(Resource<Language> resource) {
        set(resource);
    }

    public Set<Language> supported() {
        return format.keySet();
    }

    public void set(Resource<Language> resource) {
        for (Language lang : resource.getNames()) {
            try {
                Object2ObjectLinkedOpenHashMap<String, String> map = new Object2ObjectLinkedOpenHashMap<>();
                for (String res : resource.read(lang)) {
                    JSONObject json = new JSONObject(res);
                    for (String s : json.keySet()) {
                        map.put(s, json.getString(s));
                    }
                }
                EntrustExecution.executeNull(format.get(lang), m -> {
                    for (String s : map.keySet()) {
                        m.put(s, map.get(s));
                    }
                }, nu -> format.put(lang, map));
            } catch (Exception e) {

            }
        }
    }

    public abstract T format(String key, Object... args);

    public String formatted(String key, Object... args) {
        return formatted(SharedVariables.getLanguage(), key, args);
    }

    public String formatted(User user, String key, Object... args) {
        return formatted(user.getLanguage(), key, args);
    }

    public String formatted(Language language, String key, Object... args) {
        try {
            Receptacle<String> formatReturn = new Receptacle<>(format.get(language).get(key));

            if (formatReturn.get() == null) {
                formatReturn.set(key);
            }

            for (Object o : args) {
                if (o instanceof TranslatableText translatable) {
                    o = formatted(language, translatable.getKey(), translatable.getArgs());
                }
                final String str = o.toString();
                try {
                    EntrustExecution.tryTemporary(() -> formatReturn.set(formatReturn.get().replaceFirst("%s", format.get(language).get(str))), ex -> formatReturn.set(formatReturn.get().replaceFirst("%s", str)));
                } catch (Exception ex) {
                    return formatReturn.get();
                }
            }
            return formatReturn.get();
        } catch (Exception e) {
            return key;
        }
    }
}