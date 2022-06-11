package com.github.cao.awa.modmdo.format;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.lang.Dictionary;
import com.github.cao.awa.modmdo.lang.*;
import com.github.cao.awa.modmdo.resourceLoader.*;
import com.github.cao.awa.modmdo.utils.usr.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import org.json.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

public abstract class TextFormat<T> {
    protected final Object2ObjectLinkedOpenHashMap<String, Object2ObjectLinkedOpenHashMap<String, String>> format = new Object2ObjectLinkedOpenHashMap<>();

    public TextFormat(Resource<String> resource) {
        set(resource);
    }

    public void set(Resource<String> resource) {
        for (String lang : resource.getNames()) {
            try {
                Object2ObjectLinkedOpenHashMap<String, String> map = new Object2ObjectLinkedOpenHashMap<>();
                for (String res : resource.read(lang)) {
                    JSONObject json = new JSONObject(res);
                    for (String s : json.keySet()) {
                        map.put(s, json.getString(s));
                    }
                }
                EntrustExecution.executeNull(format.get(lang), m -> m.putAll(map), nu -> format.put(lang, map));
            } catch (Exception e) {

            }
        }
    }

    public Set<String> supported() {
        return format.keySet();
    }

    public abstract T format(String key, Object... args);

    public String formatted(String key, Object... args) {
        return formatted(getLanguage(), key, args);
    }

    public String formatted(User user, String key, Object... args) {
        return formatted(user.getLanguage(), key, args);
    }

    public String formatted(Language lang, String key, Object... args) {
        return formatted(lang.getName(), key, args);
    }

    public String formatted(Dictionary lang, String key, Object... args) {
        return formatted(lang.name(), key, args);
    }

    private String formatted(String lang, String key, Object... args) {
        try {
            Receptacle<String> language = new Receptacle<>(format.containsKey(lang) ? lang : getLanguage().getName());
            if (format.get(language.get()).get(key) == null) {
                language.set(getLanguage().getName());
            }
            Receptacle<String> formatReturn = new Receptacle<>(format.get(language.get()).get(key));

            if (formatReturn.get() == null) {
                formatReturn.set(key);
            }

            for (Object o : args) {
                if (o instanceof Translatable translatable) {
                    o = formatted(language.get(), translatable.getKey(), translatable.getArgs());
                }
                final String str = o.toString();
                try {
                    EntrustExecution.tryTemporary(() -> formatReturn.set(formatReturn.get().replaceFirst("%s", format.get(language.get()).get(str))), ex -> formatReturn.set(formatReturn.get().replaceFirst("%s", str)));
                } catch (Exception ex) {
                    return formatReturn.get();
                }
            }
            return formatReturn.get();
        } catch (Exception e) {
            return key;
        }
    }

    public String auto(String key, Object... args) {
        ObjectOpenHashSet<String> languages = EntrustParser.operation(new ObjectOpenHashSet<>(), set -> {
            set.addAll(format.keySet());
            set.remove(getLanguage().getName());
        });
        String preselection = formatted(getLanguage().getName(), key, args);
        if (preselection.equals(key)) {
            for (String name : languages) {
                preselection = formatted(name, key, args);
                if (!preselection.equals(key)) {
                    return preselection;
                }
            }
        }
        return preselection;
    }
}