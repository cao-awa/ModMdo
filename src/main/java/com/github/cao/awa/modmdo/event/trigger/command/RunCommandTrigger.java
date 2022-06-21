package com.github.cao.awa.modmdo.event.trigger.command;

import com.github.cao.awa.modmdo.event.*;
import com.github.cao.awa.modmdo.event.trigger.*;
import com.github.cao.awa.modmdo.event.trigger.trace.*;
import org.json.*;

public class RunCommandTrigger extends ModMdoEventTrigger<ModMdoEvent<?>> {
    private String command;

    @Override
    public ModMdoEventTrigger<ModMdoEvent<?>> prepare(ModMdoEvent<?> event, JSONObject metadata, TriggerTrace triggerTrace) {
        command = metadata.getString("command");
        setServer(event.getServer());
        return this;
    }

    @Override
    public void action() {
        getServer().getCommandManager().execute(getServer().getCommandSource(), command);
    }
}
