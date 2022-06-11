package com.github.cao.awa.modmdo.network.forwarder.process;

import com.github.cao.awa.modmdo.certificate.*;
import com.github.cao.awa.modmdo.develop.text.*;
import com.github.cao.awa.modmdo.network.forwarder.builder.*;
import com.github.cao.awa.modmdo.network.forwarder.connection.*;
import com.github.cao.awa.modmdo.network.forwarder.connection.setting.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.cao.awa.modmdo.utils.text.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.operational.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.network.*;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import org.json.*;

import java.net.*;

public class ModMdoDataProcessor {
    public static final int MINIMUM_COMPATIBILITY = SharedVariables.MODMDO_VERSION;
    public static final String DEFAULT_CHAT_FORMAT = "§7[%server]<%name> %msg";
    public static final String CONSOLE_CHAT_FORMAT = "[%server]<%name> %msg";
    private final ModMdoClientConnection modMdoConnection;
    private final ModMdoPacketBuilder builder;
    private final InetSocketAddress address;
    private final Object2ObjectRBTreeMap<String, OperationalLong> packetsOutRecord = new Object2ObjectRBTreeMap<>();
    private final Object2ObjectRBTreeMap<String, OperationalLong> packetsRecord = new Object2ObjectRBTreeMap<>();
    private final OperationalLong trafficInRecord = new OperationalLong();
    private final OperationalLong trafficOutRecord = new OperationalLong();
    private final long connected;
    private String status = "connected-not-login";
    private MinecraftServer server;
    private boolean disconnected = false;
    private long logged;
    private long lastKeepAlive = 0;
    private long lastDataPacket = TimeUtil.millions();
    private boolean trafficking = false;

    public ModMdoDataProcessor(MinecraftServer server, InetSocketAddress address, ClientConnection connection, NetworkSide side) {
        this.server = server;
        this.address = address;
        builder = new ModMdoPacketBuilder(side);
        modMdoConnection = new ModMdoClientConnection(server, connection);
        EntrustExecution.tryTemporary(() -> modMdoConnection.setMaxLoginMillion(SharedVariables.config.getConfigLong("modmdo_connection_max_login_time")));
        if (side == NetworkSide.CLIENTBOUND) {
            modMdoConnection.setLogged(true);
            modMdoConnection.setIdentifier(SharedVariables.config.getConfigString("identifier"));
            modMdoConnection.setName(SharedVariables.config.getConfigString("server_name"));
        } else {
            status = "connected-actively";
        }
        connected = TimeUtil.millions();
    }

    public long getConnectedMillions() {
        return TimeUtil.millions() - logged;
    }

    public void process(CustomPayloadC2SPacket packet) {
//        trafficInRecord.add(packet.getData().readableBytes());
        process(packet.getChannel(), packet.getData());
    }

    private void process(Identifier channel, PacketByteBuf packet) {
        try {
            if (SharedVariables.DATA_CHANNEL.equals(channel)) {
                Identifier sign = packet.readIdentifier();

                if (SharedVariables.LOGIN_CHANNEL.equals(sign)) {
                    JSONObject data = new JSONObject(packet.readString());
                    EntrustExecution.tryTemporary(() -> {
                        onLogin(data.getString("name"), data.getString("identifier"), data.getInt("version"));
                    }, () -> {
                        disconnect("modmdo.connection.server.internal.error");
                    });
                } else if (modMdoConnection.isLogged() && SharedVariables.DATA_CHANNEL.equals(sign)) {
                    String target = packet.readString();
                    String data = packet.readString();

                    if (packet.isReadable()) {
                        throw new IllegalStateException("content error");
                    }

                    EntrustExecution.executeNull(packetsRecord.get(target), OperationalLong::add, asNull -> packetsRecord.put(target, new OperationalLong(1)));

                    switch (target) {
                        case "settings" -> getModMdoConnection().setSetting(new ModMdoConnectionSetting(new JSONObject(data)));
                        case "chat" -> onChat(new JSONObject(data));
                        case "disconnect" -> onDisconnect(data);
                        case "login-success" -> onLoginSuccess();
                        case "player-join" -> onPlayerJoin(data);
                        case "player-quit" -> onPlayerQuit(data);
                        case "traffic" -> onTraffic(new JSONObject(data));
                        case "traffic-result" -> onTrafficResult(new JSONObject(data));
                    }

                    if (! "keepalive".equals(target)) {
                        lastDataPacket = TimeUtil.millions();
                        status = "connected-actively";
                    }
                }
            }
        } catch (Exception e) {
            SharedVariables.LOGGER.info("packet content is not in compliance, will not be process ");
            while (packet.isReadable()) {
                packet.readString();
            }
        }
    }

    private void onPlayerJoin(String name) {
        if (SharedVariables.config.getConfigBoolean("modmdo_connection_player_join_accept")) {
            EntrustExecution.tryFor(server.getPlayerManager().getPlayerList(), player -> {
                Literal message = SharedVariables.minecraftTextFormat.format(SharedVariables.loginUsers.getUser(player), "modmdo.connection.multiplayer.player.joined", getModMdoConnection().getName(), name);
                player.sendMessage(message.text(), false);
            });
            SharedVariables.LOGGER.info(SharedVariables.consoleTextFormat.format("modmdo.connection.multiplayer.player.joined", getModMdoConnection().getName(), name));
        }
    }

    private void onPlayerQuit(String name) {
        if (SharedVariables.config.getConfigBoolean("modmdo_connection_player_quit_accept")) {
            EntrustExecution.tryFor(server.getPlayerManager().getPlayerList(), player -> {
                Literal message = SharedVariables.minecraftTextFormat.format(SharedVariables.loginUsers.getUser(player), "modmdo.connection.multiplayer.player.left", getModMdoConnection().getName(), name);
                player.sendMessage(message.text(), false);
            });
            SharedVariables.LOGGER.info(SharedVariables.consoleTextFormat.format("modmdo.connection.multiplayer.player.left", getModMdoConnection().getName(), name));
        }
    }

    private void onChat(JSONObject chat) {
        if (SharedVariables.config.getConfigBoolean("modmdo_connection_chatting_accept")) {
            Literal message = TextUtil.literal(SharedVariables.config.getConfigString("modmdo_connection_chatting_format").replace("%server", getModMdoConnection().getName()).replace("%name", chat.getString("player")).replace("%msg", chat.getString("msg")));
            EntrustExecution.tryFor(server.getPlayerManager().getPlayerList(), player -> {
                player.sendMessage(message.text(), false);
            });
            SharedVariables.LOGGER.info(CONSOLE_CHAT_FORMAT.replace("%server", getModMdoConnection().getName()).replace("%name", chat.getString("player")).replace("%msg", chat.getString("msg")));
        }
    }

    private void onLogin(String name, String identifier, int version) {
        try {
            String selfName = SharedVariables.config.getConfigString("server_name");
            if (selfName == null || "".equals(selfName)) {
                Translatable rejectReason = TextUtil.translatable("modmdo.connection.not.ready");
                modMdoConnection.send(builder.getBuilder().buildDisconnect("modmdo.connection.not.ready"));
                modMdoConnection.disconnect(rejectReason.text());
                SharedVariables.modmdoConnections.remove(this);
                return;
            }
            if (identifier.equals(SharedVariables.config.getConfigString("identifier"))) {
                Translatable rejectReason = TextUtil.translatable("modmdo.connection.cannot.connect.to.self", SharedVariables.config.get("server_name"));
                modMdoConnection.send(builder.getBuilder().buildDisconnect(rejectReason.getKey()));
                modMdoConnection.disconnect(rejectReason.text());
                SharedVariables.modmdoConnections.remove(this);
                return;
            }
            if (name.equals("")) {
                Translatable rejectReason = TextUtil.translatable("modmdo.connection.check.failed.need.you.name", SharedVariables.config.get("server_name"));
                modMdoConnection.send(builder.getBuilder().buildDisconnect(rejectReason.getKey()));
                modMdoConnection.disconnect(rejectReason.text());
                SharedVariables.modmdoConnections.remove(this);
            } else {
                SharedVariables.LOGGER.info("ModMdo Connection \"" + name + "\" try logging to server");

                Translatable rejectReason = TextUtil.translatable("modmdo.connection.check.failed", SharedVariables.config.get("server_name"));
                boolean reject = false;

                if (version < MINIMUM_COMPATIBILITY) {
                    onLoginReject(name, TextUtil.translatable("modmdo.connection.cannot.compatible", SharedVariables.config.get("server_name")));
                    return;
                } else {
                    if (SharedVariables.modmdoConnectionAccepting.isValid()) {
                        if (EntrustParser.trying(() -> ! SharedVariables.modmdoConnectionWhitelist.containsIdentifier(identifier), () -> true)) {
                            SharedVariables.modmdoConnectionWhitelist.put(name, new PermanentCertificate(name, identifier, null));
                            SharedVariables.saveVariables();
                            SharedVariables.modmdoConnectionAccepting = new TemporaryCertificate("", - 1, - 1);
                        }
                    }
                }

                for (ModMdoDataProcessor processor : SharedVariables.modmdoConnections) {
                    if (identifier.equals(processor.getModMdoConnection().getIdentifier())) {
                        reject = true;
                        rejectReason = TextUtil.translatable("modmdo.connection.already.connect", SharedVariables.config.get("server_name"));
                        break;
                    }
                }

                if (! reject && SharedVariables.modmdoConnectionWhitelist.containsIdentifier(identifier)) {
                    SharedVariables.LOGGER.info("ModMdo Connection \"" + name + "\" success to login");
                    modMdoConnection.setLogged(true);
                    modMdoConnection.setIdentifier(identifier);
                    updateSetting();
                    modMdoConnection.send(builder.getBuilder().buildLoginSuccess());
                } else {
                    onLoginReject(name, rejectReason);
                }
            }
        } catch (Exception e) {

        }
    }

    public void onLoginReject(String name, Translatable reason) {
        SharedVariables.LOGGER.warn("ModMdo Connection \"" + name + "\" failed to login");
        modMdoConnection.send(builder.getBuilder().buildDisconnect(reason.getKey()));
        modMdoConnection.disconnect(reason.text());
        SharedVariables.modmdoConnections.remove(this);
    }

    public void updateSetting() {
        modMdoConnection.send(builder.getBuilder().buildSetting(ModMdoConnectionSetting.localSettings()));
        SharedVariables.updateModMdoConnectionsNames(server);
    }

    public ModMdoClientConnection getModMdoConnection() {
        return modMdoConnection;
    }

    private void onLoginSuccess() {
        updateSetting();
        EntrustExecution.tryFor(server.getPlayerManager().getPlayerList(), player -> {
            String message = SharedVariables.minecraftTextFormat.format(SharedVariables.loginUsers.getUser(player), "modmdo.connection.login.success", getSetting().getName()).getString();
            player.sendMessage(TextUtil.literal("[ModMdo Connection] " + message).text(), false);
            SharedVariables.LOGGER.info(message);
        });
        logged = TimeUtil.millions();
        status = "connected-actively";
    }

    public void process(CustomPayloadS2CPacket packet) {
//        trafficInRecord.add(packet.getData().readableBytes());
        process(packet.getChannel(), packet.getData());
    }

    public void sendChat(String message, String player) {
        if (getSetting().isChat() && SharedVariables.config.getConfigBoolean("modmdo_connection_chatting_forward")) {
            modMdoConnection.send(builder.getBuilder().buildChat(message, player));
        }
    }

    public ModMdoConnectionSetting getSetting() {
        return modMdoConnection.getSetting();
    }

    public void sendPlayerJoin(String name) {
        if (getSetting().isPlayerJoin() && SharedVariables.config.getConfigBoolean("modmdo_connection_player_join_forward")) {
            modMdoConnection.send(builder.getBuilder().buildPlayerJoin(name));
        }
    }

    public void sendPlayerQuit(String name) {
        if (getSetting().isPlayerQuit() && SharedVariables.config.getConfigBoolean("modmdo_connection_player_quit_forward")) {
            modMdoConnection.send(builder.getBuilder().buildPlayerQuit(name));
        }
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void tick(MinecraftServer server) {
        this.server = server;
        EntrustExecution.executeNull(getConnection(), connection -> {
            if (connection.isOpen()) {
                connection.tick();
                if (builder.getSide() == NetworkSide.CLIENTBOUND && TimeUtil.millions() - lastKeepAlive > 15000) {
                    sendKeepAlive(lastKeepAlive);
                    lastKeepAlive = TimeUtil.millions();
                }
                if (! modMdoConnection.isLogged() && modMdoConnection.getMaxLoginMillion() < (TimeUtil.millions() - connected)) {
                    disconnect("modmdo.connection.login.time.too.long");
                }
                if (TimeUtil.millions() - lastDataPacket > 60000) {
                    status = "connected-silence";
                }
            } else {
                if (! disconnected) {
                    onDisconnect("modmdo.connection.disconnect.unknown");
                }
                SharedVariables.modmdoConnections.remove(this);
            }
        }, connection -> {
            if (! disconnected) {
                onDisconnect("modmdo.connection.disconnect.network.error");
            }
            SharedVariables.modmdoConnections.remove(this);
        });
    }

    public void sendKeepAlive(long lastKeepAlive) {
        send(builder.getBuilder().buildKeepAlive(lastKeepAlive));
    }

    public ClientConnection getConnection() {
        return modMdoConnection.getConnection();
    }

    public void disconnect() {
        disconnected = true;
        if (getSetting().isTesting()) {
            send(builder.getBuilder().buildTraffic(trafficInRecord, packetsRecord));
        }
        send(builder.getBuilder().buildDisconnect("modmdo.connection.target.disconnect.initiative"));
        onDisconnect("modmdo.connection.target.disconnect.initiative");
        modMdoConnection.disconnect(TextUtil.translatable("modmdo.connection.target.disconnect.initiative").text());
    }

    public void send(Packet<?> packet) {
        modMdoConnection.send(packet);
    }

    private void onDisconnect(String message) {
        EntrustExecution.tryFor(() -> server.getPlayerManager().getPlayerList(), player -> player.sendMessage(TextUtil.literal("[ModMdo Connection] " + SharedVariables.minecraftTextFormat.format(SharedVariables.loginUsers.getUser(player), message, getAddress()).getString()).text(), false));
        disconnected = true;
        status = "disconnected";
        EntrustExecution.notNull(modMdoConnection.getConnection(), connection -> connection.disconnect(TextUtil.translatable(message).text()));
        SharedVariables.updateModMdoConnectionsNames(server);
        SharedVariables.LOGGER.info(SharedVariables.consoleTextFormat.format(message, getAddress()));
        if (SharedVariables.testing) {
            traffic();
        }
    }

    public void traffic() {
        StringBuilder builder = new StringBuilder();
        long time = TimeUtil.millions() - connected;
        builder.append("----------Connection Testing----------").append("\n");
        builder.append("Connection keep: ").append("\n    ").append(TimeUtil.processRemainingDays(time)).append("d ").append(TimeUtil.processRemainingHours(time)).append("h ").append(TimeUtil.processRemainingMinutes(time)).append("m ").append(TimeUtil.processRemainingSeconds(time)).append("s").append("\n");
        builder.append("Packets processed: ").append("\n");
        for (String name : packetsRecord.keySet()) {
            builder.append("    ").append(name).append(": ").append(packetsRecord.get(name).get()).append("\n");
        }
        builder.append("Packets sent(uncertain?): ").append("\n");
        for (String name : packetsOutRecord.keySet()) {
            builder.append("    ").append(name).append(": ").append(packetsOutRecord.get(name).get()).append("\n");
        }
        builder.append("Traffic: \n    ").append("in: ").append(trafficInRecord.get()).append("bytes").append("\n    ").append("out: ").append(trafficOutRecord.get()).append("bytes").append("\n");
        builder.append("Address: \n    ").append(getAddress().toString()).append("\n");
        builder.append("Keepalive-interval: \n    ").append("10s").append("\n");
        builder.append("Status: \n    ").append(status).append("\n");
        builder.append("Side: \n    ").append(this.builder.getSide().name());
        SharedVariables.LOGGER.info(builder.toString());
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public void onTraffic(JSONObject json) {
        trafficOutRecord.set(json.getLong("traffic-in"));
        JSONObject packets = json.getJSONObject("packets-processed");
        for (String s : packets.keySet()) {
            packetsOutRecord.put(s, new OperationalLong(packets.getLong(s)));
        }
        send(builder.getBuilder().buildTrafficResult(trafficInRecord, packetsRecord));
    }

    public void onTrafficResult(JSONObject json) {
        trafficOutRecord.set(json.getLong("traffic-in"));
        JSONObject packets = json.getJSONObject("packets-processed");
        for (String s : packets.keySet()) {
            packetsOutRecord.put(s, new OperationalLong(packets.getLong(s)));
        }
        if (trafficking) {
            traffic();
            trafficking = false;
        }
    }

    public void sendTraffic() {
        trafficking = true;
        send(builder.getBuilder().buildTraffic(trafficInRecord, packetsRecord));
    }

    public void disconnect(String reason) {
        disconnected = true;
        modMdoConnection.send(builder.getBuilder().buildDisconnect(reason));
        onDisconnect(reason);
        modMdoConnection.disconnect(TextUtil.translatable(reason).text());
    }
}
