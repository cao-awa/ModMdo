package com.github.cao.awa.modmdo.cavas;

import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.usr.User;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import org.json.JSONObject;

import java.security.SecureRandom;

public class CavaUtil {
    private final Object2ObjectRBTreeMap<Object, Cava> cavas = new Object2ObjectRBTreeMap<>();
    private final ObjectRBTreeSet<String> cavaMessages = new ObjectRBTreeSet<>();

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

        SharedVariables.updateCavas();
        return cava;
    }

    public Cava get() {
        try {
            return cavas.get(cavas.keySet().toArray()[new SecureRandom().nextInt(cavas.size())]);
        } catch (Exception e) {
            return null;
        }
    }

    public void deleteCava(String id) {
        if(cavas.get(id) == null)
            throw new IllegalArgumentException(id + " not found");

        cavas.remove(id);

        SharedVariables.updateCavas();
    }
}
