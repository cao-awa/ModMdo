package com.github.zhuaidadaya.modmdo.login.server;

import com.github.zhuaidadaya.modmdo.login.token.ClientEncryptionToken;
import com.github.zhuaidadaya.modmdo.login.token.ServerEncryptionToken;
import com.github.zhuaidadaya.modmdo.usr.User;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

public class ServerLogin {
    public void login(String data1, String data2, String data3, String data4, String data5, String data6) {
        int level = 1;
        if(data3.equals("ops"))
            level = 4;

        if(! data1.equals("")) {
            if(enableEncryptionToken) {
                try {
                    if(data4.equals(modMdoToken.getServerToken().checkToken(data3))) {
                        LOGGER.info("login player: " + data1);

                        loginUsers.put(new User(data2, data1, level, new ClientEncryptionToken(data4, data5, data3, data6)));
                    } else {
                        rejectUsers.put(new User(data2, data1, level));
                    }
                } catch (NullPointerException e) {
                    modMdoToken.setServerToken(ServerEncryptionToken.createServerEncryptionToken());

                    rejectUsers.put(new User(data2, data1, level));

                    saveToken();

                    updateModMdoVariables();
                }
            } else {
                try {
                    LOGGER.info("login player: " + data1);

                    loginUsers.put(new User(data2, data1, level, new ClientEncryptionToken(data4, data5, data3, data6)));
                } catch (Exception e) {

                }
            }
        }
    }

    public void logout(ServerPlayerEntity player) {
        if(loginUsers.getUser(player).getClientToken() != null) {
            LOGGER.info("logout player: " + player.getUuid().toString());
            LOGGER.info("canceling player token for: " + player.getUuid().toString());
        }
        try {
            loginUsers.removeUser(player);
        } catch (Exception e) {

        }
    }
}
