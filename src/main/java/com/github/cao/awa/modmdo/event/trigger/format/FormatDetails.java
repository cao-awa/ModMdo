package com.github.cao.awa.modmdo.event.trigger.format;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.lang.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;
import org.json.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.getLanguage;
import static com.github.cao.awa.modmdo.storage.SharedVariables.minecraftTextFormat;

public class FormatDetails {
    private String key;
    private final ObjectArrayList<Receptacle<String>> args = new ObjectArrayList<>();
    private Dictionary dictionary;
    private boolean active = true;

    public FormatDetails(String key, JSONArray array) {
        set(key, array);
    }

    public void set(String key, JSONArray array) {
        set(key, array, null);
    }

    public void set(String key, JSONArray array, Dictionary dictionary) {
        EntrustParser.operation(args, list -> {
            for (int i = 0; i < array.length(); i++) {
                list.add(new Receptacle<>(array.get(i).toString()));
            }
        });
        this.key = key;
        this.dictionary = dictionary;
    }

    public FormatDetails() {

    }

    public Literal format() {
        for (Receptacle<String> s : args) {
            if (s.get().startsWith("{")) {
                String name = EntrustParser.trying(() -> {
                    JSONObject json = new JSONObject(s.get());
                    return json.getString("name");
                }, ex -> {
                    throw new IllegalStateException("Cannot format variable", ex);
                });
                if (name == null) {
                    return null;
                }
                EntrustExecution.tryTemporary(() -> {
                    ModMdoEventTrigger.BASE_FORMATTER.get("^{variable}").accept(s.setSub(name));
                }, e -> {
                    throw new IllegalStateException("Cannot format variable", e);
                });
            } else {
                EntrustExecution.tryTemporary(() -> ModMdoEventTrigger.BASE_FORMATTER.get(s.get()).accept(s), ex -> {
                });
            }
        }
        Object[] objs = EntrustParser.operation(new Object[args.size()], e -> {
            for (int i = 0; i < args.size(); i++) {
                e[i] = args.get(i).get();
            }
        });

        return minecraftTextFormat.format(new Dictionary(dictionary == null ? getLanguage().getName() : dictionary.name()), key, objs);
    }
}
