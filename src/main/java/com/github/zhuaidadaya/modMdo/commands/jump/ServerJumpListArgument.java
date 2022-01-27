package com.github.zhuaidadaya.modMdo.commands.jump;

import java.util.Collection;
import java.util.Collections;

import static com.github.zhuaidadaya.modMdo.storage.Variables.servers;

public class ServerJumpListArgument {
    public Collection<String> getServersName() {
        if(servers.getServersName().size() > 0) {
            return servers.getServersName();
        } else {
            return Collections.emptyList();
        }
    }
}
