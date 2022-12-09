package com.github.cao.awa.modmdo.event.variable;

import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import org.json.*;

import java.io.*;

public class ModMdoVariableBuilder {
    public ModMdoPersistent<?> build(File file, JSONObject json) {
        JSONObject variable = json.getJSONObject("variable");
        Receptacle<ModMdoPersistent<?>> receptacle = new Receptacle<>(null);
        EntrustEnvironment.trys(
                () -> {
                    receptacle.set((ModMdoPersistent<?>) Class.forName(variable.getString("instanceof"))
                                                              .getDeclaredConstructor()
                                                              .newInstance());
                    EntrustEnvironment.trys(
                            () -> receptacle.get()
                                            .build(
                                                    file,
                                                    variable
                                            ),
                            ex -> {
                                SharedVariables.LOGGER.warn(
                                        "Cannot build persistent: " + (variable.has("name") ?
                                                                       variable.getString("name") :
                                                                       file.getPath()),
                                        ex
                                );
                                receptacle.set(null);
                            }
                    );
                },
                ex -> SharedVariables.LOGGER.warn(
                        "Cannot build persistent: " + (variable.has("name") ?
                                                       variable.getString("name") :
                                                       file.getPath()),
                        ex
                )
        );
        if (receptacle.get() == null) {
            SharedVariables.LOGGER.warn("Failed build persistent: " + (variable.has("name") ?
                                                                       variable.getString("name") :
                                                                       file.getPath()));
        }
        return receptacle.get();
    }
}
