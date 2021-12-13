package com.github.zhuaidadaya.modMdo.mixins;

import com.github.zhuaidadaya.modMdo.usr.User;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

import static com.github.zhuaidadaya.modMdo.storage.Variables.*;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayerEntity player;

    @Inject(method = "onCustomPayload", at = @At("HEAD"), cancellable = true)
    private void onCustomPayload(CustomPayloadC2SPacket packet, CallbackInfo ci) {
        Identifier channel = new Identifier("");
        try {
            channel = packet.getChannel();
        } catch (Exception e) {
        }

        PacketByteBuf packetByteBuf = null;
        try {
            packetByteBuf = new PacketByteBuf(packet.getData().copy());
        } catch (Exception e) {
        }

        String data1 = "";
        try {
            data1 = packetByteBuf.readString();
        } catch (Exception e) {
        }

        String data2 = "";
        try {
            data2 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        String data3 = "";
        try {
            data3 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        String data4 = "";
        try {
            data4 = packetByteBuf.readString();
        } catch (Exception e) {

        }

        //        System.out.println(channel);
        //        System.out.println(data1);
        //        System.out.println(data2);
        //        System.out.println(data3);

        if(channel.equals(tokenChannel)) {
            int level = 1;
            if(data3.equals("ops"))
                level = 4;

            if(! data1.equals("")) {
                if(data4.equals(modMdoServerToken.getJSONObject("server").get(data3).toString())) {
                    LOGGER.info("login player: " + data1);

                    loginUsers.put(data1, new User(data2, data1,level).toJSONObject());
                    cacheUsers.removeUser(loginUsers.getUser(data1));
                }
            }
        }

        if(channel.equals(connectingChannel)) {
            if(! data1.equals("") & ! data2.equals("")) {
                cacheUsers.put(data1, new User(data2, data1).toJSONObject());
            }
        }

        ci.cancel();
    }

    @Inject(method = "onDisconnected", at = @At("HEAD"))
    public void onDisconnected(Text reason, CallbackInfo ci) {
        LOGGER.info("logout player: " + player.getUuid().toString());
        LOGGER.info("canceling player token for: " + player.getUuid().toString());
        try {
            loginUsers.removeUser(player);
            cacheUsers.removeUser(player);
        } catch (Exception e) {

        }
    }

    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    public void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onVehicleMove", at = @At("HEAD"), cancellable = true)
    public void onVehicleMove(VehicleMoveC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerInput", at = @At("HEAD"), cancellable = true)
    public void onPlayerInput(PlayerInputC2SPacket packet, CallbackInfo ci) {
        if(! loginUsers.hasUser(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "requestTeleport(DDDFFLjava/util/Set;Z)V", at = @At("HEAD"), cancellable = true)
    public void requestTeleport(double x, double y, double z, float yaw, float pitch, Set<PlayerPositionLookS2CPacket.Flag> flags, boolean shouldDismount, CallbackInfo ci) {
        if(! loginUsers.hasUser(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true)
    private void executeCommand(String input, CallbackInfo ci) {
        LOGGER.info(player.getName().asString() + "(" + player.getUuid().toString() + ") run the command: " + input);
        if(! loginUsers.hasUser(player)) {
            ci.cancel();
        }
    }
}