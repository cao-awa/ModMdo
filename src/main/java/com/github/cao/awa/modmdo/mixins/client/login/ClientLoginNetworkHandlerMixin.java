package com.github.cao.awa.modmdo.mixins.client.login;

import com.github.cao.awa.modmdo.develop.text.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.client.util.*;
import net.minecraft.network.*;
import net.minecraft.network.encryption.*;
import net.minecraft.network.packet.c2s.login.*;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.text.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import javax.crypto.*;
import java.math.*;
import java.security.*;
import java.util.*;
import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ClientLoginNetworkHandler.class)
public abstract class ClientLoginNetworkHandlerMixin {
    @Shadow @Final private Consumer<Text> statusConsumer;

    @Shadow @Final private ClientConnection connection;

    @Shadow @Nullable protected abstract Text joinServerSession(String serverId);

    @Shadow @Final private MinecraftClient client;

    private boolean isModMdo;

    /**
     * @author 草awa
     * @reason
     */
    @Overwrite
    public void onHello(LoginHelloS2CPacket packet) {
        isModMdo = EntrustParser.tryCreate(() -> Arrays.equals(NONCE, packet.getNonce()), false);
        Cipher cipher;
        Cipher cipher2;
        String string;
        LoginKeyC2SPacket loginKeyC2SPacket;
        try {
            SecretKey secretKey = NetworkEncryptionUtils.generateKey();
            PublicKey publicKey = packet.getPublicKey();
            string = new BigInteger(NetworkEncryptionUtils.generateServerId(packet.getServerId(), publicKey, secretKey)).toString(16);
            cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
            cipher2 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
            loginKeyC2SPacket = new LoginKeyC2SPacket(secretKey, publicKey, packet.getNonce());
        } catch (NetworkEncryptionException var8) {
            throw new IllegalStateException("Protocol error", var8);
        }

        this.statusConsumer.accept(Translatable.translatable("connect.authorizing").text());
        NetworkUtils.EXECUTOR.submit(() -> {
            Text text = this.joinServerSession(string);
            if (!isModMdo) {
                if (text != null) {
                    if (this.client.getCurrentServerEntry() == null || ! this.client.getCurrentServerEntry().isLocal()) {
                        this.connection.disconnect(text);
                        return;
                    }

                    TRACKER.warn(text.getString());
                }
            }

            this.statusConsumer.accept(Translatable.translatable("connect.encrypting").text());
            this.connection.send(loginKeyC2SPacket, (future) -> {
                this.connection.setupEncryption(cipher, cipher2);
            });
        });
    }
}
