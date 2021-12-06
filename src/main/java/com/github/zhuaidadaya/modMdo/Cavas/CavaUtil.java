package com.github.zhuaidadaya.modMdo.Cavas;

import com.github.zhuaidadaya.modMdo.Usr.User;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Random;

import static com.github.zhuaidadaya.modMdo.Storage.Variables.updateCavas;

public class CavaUtil {
    private final LinkedHashMap<Object, Cava> cavas = new LinkedHashMap<>();
    private final LinkedHashSet<String> cavaMessages = new LinkedHashSet<>();

    public CavaUtil() {

    }

    public CavaUtil(JSONObject json) {
        for(Object o : json.keySet()) {
            Cava cava = new Cava(json.getJSONObject(o.toString()));
            cavas.put(o.toString(), cava);
            cavaMessages.add(cava.getMessage());
        }
    }

    public void put(Cava cava) {
        cavas.put(cava.getID(), cava);
    }

    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        for(Object o : cavas.keySet()) {
            Cava cava = cavas.get(o.toString());
            json.put(cava.getID(), cava.toJSONObject());
        }
        return json;
    }

    public Cava createCava(User user, String message) {
        if(cavaMessages.contains(message))
            throw new IllegalArgumentException("already exists a Cava for target message");

        Cava cava = new Cava(user, message);
        put(cava);
        cavaMessages.add(message);

        updateCavas();
        return cava;
    }

    public Cava get() {
        return cavas.get(cavas.keySet().toArray()[new Random().nextInt(cavas.size())]);
    }

    public void deleteCava(String id) {
        if(cavas.get(id) == null)
            throw new IllegalArgumentException(id + " not found");

        cavas.remove(id);

        updateCavas();
    }
}
