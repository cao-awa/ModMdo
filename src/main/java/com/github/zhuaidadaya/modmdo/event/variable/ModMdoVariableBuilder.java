package com.github.zhuaidadaya.modmdo.event.variable;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.*;

import java.io.*;

public class ModMdoVariableBuilder {
    public ModMdoPersistent<?> build(File file, JSONObject json) {
        JSONObject variable = json.getJSONObject("variable");
        return EntrustParser.trying(() -> {
            ModMdoPersistent<?> v = (ModMdoPersistent<?>) Class.forName(variable.getString("instanceof")).getDeclaredConstructor().newInstance();
            v.build(file, variable);
            return v;
        });
    }
}
