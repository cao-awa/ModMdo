package com.github.cao.awa.modmdo.mixins.client.play;

import com.github.cao.awa.modmdo.commands.argument.*;
import com.github.cao.awa.modmdo.security.level.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.entity.player.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import com.mojang.authlib.*;
import io.netty.buffer.*;
import net.minecraft.client.*;
import net.minecraft.client.network.*;
import net.minecraft.network.*;
import net.minecraft.network.listener.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.util.*;
import org.json.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {
    @Final
    @Shadow
    private MinecraftClient client;

    @Shadow
    @Final
    private ClientConnection connection;

    @Shadow
    @Final
    private GameProfile profile;

    /**
     * 与服务端进行自定义通信
     *
     * @param packet
     *         packet
     * @param ci
     *         callback
     * @author 草二号机
     * @author 草awa
     * @author zhuaidadaya
     */
    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onOnCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            PacketByteBuf data = packet.getData();

            try {
                Identifier informationSign = EntrustParser.tryCreate(data::readIdentifier, new Identifier(""));

                if (informationSign.equals(CHECKING_CHANNEL) || informationSign.equals(LOGIN_CHANNEL)) {
                    SECURE_KEYS.load(staticConfig.getConfigJSONObject("private_key"));
                    Receptacle<String> serverId = new Receptacle<>(null);
                    if (informationSign.equals(CHECKING_CHANNEL)) {
                        EntrustExecution.tryTemporary(() -> serverId.set(data.readString()));
                    }
                    TRACKER.submit("Server are requesting login data", () -> {
                        EntrustExecution.notNull(staticConfig.get("secure_level"), level -> {
                            SECURE_KEYS.setLevel(SecureLevel.of(level));
                            TRACKER.submit("Changed config secure_level as " + level);
                        });
                        String address = EntrustParser.tryCreate(() -> {
                            String addr = connection.getAddress().toString();
                            return addr.substring(addr.indexOf("/") + 1);
                        }, connection.getAddress().toString());
                        String key = serverId.get() == null ? SECURE_KEYS.use(address, address) : SECURE_KEYS.use(serverId.get(), address);
                        if (serverId.get() != null && key != null) {
                            if (SECURE_KEYS.has(address) && ! SECURE_KEYS.has(serverId.get())) {
                                EntrustExecution.notNull(SECURE_KEYS.get(address), k -> k.setServerId(serverId.get()));
                                SECURE_KEYS.set(SECURE_KEYS.get(address).getServerId(), SECURE_KEYS.get(address));
                            }
                            SECURE_KEYS.removeAddress(address);
                            SECURE_KEYS.save();
                        }
                        connection.send(new CustomPayloadC2SPacket(CLIENT_CHANNEL, (new PacketByteBuf(Unpooled.buffer())).writeString(LOGIN_CHANNEL.toString()).writeString(profile.getName()).writeString(PlayerUtil.getId(profile).toString()).writeString(key).writeString(String.valueOf(MODMDO_VERSION)).writeString(MODMDO_VERSION_NAME)));
                    });
                }

                if (informationSign.equals(DATA_CHANNEL)) {
                    String data1 = EntrustParser.tryCreate(data::readString, "");
                    String data2 = EntrustParser.tryCreate(data::readString, "");

                    TRACKER.submit(String.format("Server are requesting client process data: type={%s} | information={%s}", data1, data2));

                    switch (data1) {
                        case "whitelist_names" -> {
                            whitelist.clear();
                            JSONObject json = new JSONObject(data2);
                            for (Object o : json.getJSONArray("names")) {
                                whitelist.put(o.toString(), null);
                            }
                        }
                        case "temporary_whitelist_names" -> {
                            temporaryStation.clear();
                            JSONObject json = new JSONObject(data2);
                            for (Object o : json.getJSONArray("names")) {
                                temporaryStation.put(o.toString(), null);
                            }
                        }
                        case "connections" -> EntrustExecution.tryTemporary(() -> {
                            modmdoConnectionNames.clear();
                            JSONObject json = new JSONObject(data2);
                            for (Object o : json.getJSONArray("names")) {
                                modmdoConnectionNames.add(o.toString());
                            }
                        });
                        case "ban_names" -> {
                            banned.clear();
                            JSONObject json = new JSONObject(data2);
                            for (Object o : json.getJSONArray("names")) {
                                banned.put(o.toString(), null);
                            }
                        }
                        case "temporary_invite" -> {
                            temporaryInvite.clear();
                            JSONObject json = new JSONObject(data2);
                            for (Object o : json.getJSONArray("names")) {
                                temporaryInvite.put(o.toString(), null);
                            }
                        }
                    }
                    ArgumentInit.init();
                }
            } catch (Exception e) {
                TRACKER.err("Error in connecting ModMdo server", e);
            }
            ci.cancel();
        }
    }
}