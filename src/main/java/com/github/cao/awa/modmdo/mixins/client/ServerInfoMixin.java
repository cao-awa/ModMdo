package com.github.cao.awa.modmdo.mixins.client;

import com.github.cao.awa.modmdo.security.level.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.client.network.*;
import org.apache.logging.log4j.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ServerInfo.class)
public class ServerInfoMixin {
    private static final Logger LOGGER = LogManager.getLogger("ModMdoServerInfo");

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void fromNbt(String name, String address, boolean local, CallbackInfo ci) {
        EntrustExecution.notNull(staticConfig.getString("secure_level"), level -> {
            SECURE_KEYS.setLevel(SecureLevel.of(level));
            LOGGER.info("Changed config secure_level as '{}'", level);
        });
        if (!SECURE_KEYS.hasAddress(address)) {
            SECURE_KEYS.keep(address, address);
            SECURE_KEYS.save();
        }
    }
}
