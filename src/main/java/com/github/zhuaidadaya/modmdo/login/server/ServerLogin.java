package com.github.zhuaidadaya.modmdo.login.server;

import com.github.zhuaidadaya.modmdo.utils.usr.User;
import com.github.zhuaidadaya.modmdo.whitelist.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ServerLogin {
    public void login(String name, String uuid, String identifier, String modmdoVersion) {
        int version = EntrustParser.tryCreate(() -> Integer.valueOf(modmdoVersion), - 1);

        if (! identifier.equals("")) {
            if (modmdoWhiteList) {
                EntrustExecution.notNull(temporaryWhitelist.get(name), e -> {
                    if (e.isValid()) {
                        if (EntrustParser.trying(() -> ! whitelist.get(name).identifier().equals(identifier), () -> true)) {
                            whitelist.put(name, new WhiteList(name, identifier));
                            updateModMdoVariables();
                        }
                    }
                    temporaryWhitelist.remove(name);
                });
                if (EntrustParser.trying(() -> ! whitelist.get(name).identifier().equals(identifier), () -> true)) {
                    rejectUsers.put(new User(name, uuid, - 1, identifier, version));
                } else {
                    LOGGER.info("login player: " + name);
                    loginUsers.put(new User(name, uuid, - 1, identifier, version));
                }
            } else {
                try {
                    LOGGER.info("login player: " + name);
                    loginUsers.put(new User(name, uuid, - 1, identifier, version));
                } catch (Exception e) {

                }
            }
        }
    }

    public void logout(ServerPlayerEntity player) {
        try {
            loginUsers.removeUser(player);
        } catch (Exception e) {

        }
    }
}
