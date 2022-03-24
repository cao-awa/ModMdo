package com.github.zhuaidadaya.modmdo.commands.jump;

import java.util.Collection;
import java.util.Collections;

import static com.github.zhuaidadaya.modmdo.storage.Variables.servers;

public class ServerJumpListArgument {
    public Collection<String> getServersName() {
        if(servers.getServersName().size() > 0) {
            return servers.getServersName();
        } else {
            return Collections.emptyList();
        }
    }
}
