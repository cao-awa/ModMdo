package com.github.cao.awa.modmdo.benchmark.connect;

import com.mojang.authlib.*;
import com.mojang.authlib.minecraft.*;
import com.mojang.authlib.yggdrasil.*;
import net.minecraft.client.util.*;
import net.minecraft.network.*;
import net.minecraft.network.encryption.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.text.*;
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;

import javax.crypto.*;
import java.math.*;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;

public class ClientLoginNetworkHandlerB implements ClientLoginPacketListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY);
    private static final MinecraftSessionService sessionService = yggdrasilAuthenticationService.createMinecraftSessionService();
    private final ClientConnection connection;

    public ClientLoginNetworkHandlerB(ClientConnection connection) {
        this.connection = connection;
    }

    public static void pre() {

    }

    public void onHello(LoginHelloS2CPacket packet) {
        Cipher cipher;
        Cipher cipher2;
        String string;
        LoginKeyC2SPacket loginKeyC2SPacket;
        try {
            SecretKey secretKey = NetworkEncryptionUtils.generateKey();
            PublicKey publicKey = packet.getPublicKey();
            string = (new BigInteger(NetworkEncryptionUtils.generateServerId(
                    packet.getServerId(),
                    publicKey,
                    secretKey
            ))).toString(16);
            cipher = NetworkEncryptionUtils.cipherFromKey(
                    2,
                    secretKey
            );
            cipher2 = NetworkEncryptionUtils.cipherFromKey(
                    1,
                    secretKey
            );
            loginKeyC2SPacket = new LoginKeyC2SPacket(
                    secretKey,
                    publicKey,
                    packet.getNonce()
            );
        } catch (NetworkEncryptionException var8) {
            throw new IllegalStateException(
                    "Protocol error",
                    var8
            );
        }

        NetworkUtils.EXECUTOR.submit(() -> {
            Text text = this.joinServerSession(string);
            if (text != null) {
                this.connection.disconnect(text);
            }

            this.connection.send(
                    loginKeyC2SPacket,
                    (future) -> this.connection.setupEncryption(
                            cipher,
                            cipher2
                    )
            );
        });
    }

    @Override
    public void onSuccess(LoginSuccessS2CPacket packet) {

    }

    @Nullable
    private Text joinServerSession(String serverId) {
        try {
            UUID uuid = UUID.randomUUID();
            this.getSessionService()
                .joinServer(
                        new GameProfile(
                                uuid,
                                "test" + RANDOM.nextInt(100000)
                        ),
                        uuid.toString(),
                        serverId
                );
            return null;
        } catch (Exception var3) {
            return null;
        }
    }

    private MinecraftSessionService getSessionService() {
        return sessionService;
    }

    public void onDisconnect(LoginDisconnectS2CPacket packet) {
        this.connection.disconnect(packet.getReason());
    }

    public void onCompression(LoginCompressionS2CPacket packet) {
        if (! this.connection.isLocal()) {
            this.connection.setCompressionThreshold(
                    packet.getCompressionThreshold(),
                    false
            );
        }

    }

    public void onQueryRequest(LoginQueryRequestS2CPacket packet) {
        this.connection.send(new LoginQueryResponseC2SPacket(
                packet.getQueryId(),
                null
        ));
    }

    public void onDisconnected(Text reason) {
        System.out.println("Disconnect: " + reason);
    }

    public ClientConnection getConnection() {
        return this.connection;
    }
}

