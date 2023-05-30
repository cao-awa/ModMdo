package com.github.cao.awa.modmdo.mixins.client.login;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.client.network.*;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.text.*;
import org.apache.logging.log4j.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ClientLoginNetworkHandler.class)
public abstract class ClientLoginNetworkHandlerMixin {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoClientLoginHandler");

    private boolean isModMdoServer;

    /**
     * Check server is ModMdo server and mark it.
     *
     * @param packet Login packet
     * @param ci Callback
     *
     * @author 草二号机
     * @author cao_awa
     */
    @Inject(method = "onHello", at = @At("HEAD"))
    private void onHello(LoginHelloS2CPacket packet, CallbackInfo ci) {
        this.isModMdoServer = EntrustEnvironment.get(
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
        );
    }

    /**
     * Let client ignored vanilla verify when server is ModMdo server.
     *
     * @param serverId ServerId
     * @param cir Callback
     *
     * @author 草二号机
     */
    @Inject(method = "joinServerSession", at = @At("RETURN"), cancellable = true)
    public void joinServerSession(String serverId, CallbackInfoReturnable<Text> cir) {
        if (this.isModMdoServer) {
            cir.setReturnValue(null);
        }
    }
}
