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
import org.apache.logging.log4j.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import javax.crypto.*;
import java.math.*;
import java.security.*;
import java.util.*;
import java.util.function.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ClientLoginNetworkHandler.class)
public abstract class ClientLoginNetworkHandlerMixin {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoClientLoginHandler");

    @Shadow
    @Final
    private Consumer<Text> statusConsumer;

    @Shadow
    @Final
    private ClientConnection connection;
    @Shadow
    @Final
    private MinecraftClient client;

    /**
     * @author cao_awa
     * @reason
     */
    @Inject(method = "onHello", at = @At("HEAD"), cancellable = true)
    public void onHello(LoginHelloS2CPacket packet, CallbackInfo ci) {
        if (EntrustEnvironment.get(
                () -> Arrays.equals(
                        EntrustEnvironment.operation(
                                new byte[22],
                                nonce -> System.arraycopy(
                                        packet.getNonce(),
                                        0,
                                        nonce,
                                        0,
                                        22
                                )
                        ),
                        MODMDO_NONCE_HEAD
                ),
                false
        )) {
            Cipher cipher;
            Cipher cipher2;
            String serverId;
            LoginKeyC2SPacket loginKeyPacket;
            try {
                SecretKey secretKey = NetworkEncryptionUtils.generateKey();
                PublicKey publicKey = packet.getPublicKey();
                serverId = new BigInteger(NetworkEncryptionUtils.generateServerId(
                        packet.getServerId(),
                        publicKey,
                        secretKey
                )).toString(16);
                cipher = NetworkEncryptionUtils.cipherFromKey(
                        2,
                        secretKey
                );
                cipher2 = NetworkEncryptionUtils.cipherFromKey(
                        1,
                        secretKey
                );
                loginKeyPacket = new LoginKeyC2SPacket(
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

            this.statusConsumer.accept(Translatable.translatable("connect.authorizing")
                                                   .text());
            NetworkUtils.EXECUTOR.submit(() -> {
                this.joinServerSession(serverId);

                this.statusConsumer.accept(Translatable.translatable("connect.encrypting")
                                                       .text());
                this.connection.send(
                        loginKeyPacket,
                        (future) -> this.connection.setupEncryption(
                                cipher,
                                cipher2
                        )
                );
            });

            ci.cancel();
        }
    }

    @Shadow
    @Nullable
    protected abstract Text joinServerSession(String serverId);
}
