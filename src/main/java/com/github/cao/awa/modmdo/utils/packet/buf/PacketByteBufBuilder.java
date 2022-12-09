package com.github.cao.awa.modmdo.utils.packet.buf;

import com.github.cao.awa.modmdo.utils.packet.sender.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import org.json.*;

public record PacketByteBufBuilder(PacketSender<?> sender, PacketByteBuf buf) {
    public PacketByteBufBuilder write(Identifier identifier) {
        this.buf.writeIdentifier(identifier);
        return this;
    }

    public PacketByteBufBuilder write(JSONObject json) {
        this.buf.writeString(json.toString());
        return this;
    }

    public PacketByteBufBuilder write(String str) {
        this.buf.writeString(str);
        return this;
    }

    public PacketByteBufBuilder write(int i) {
        this.buf.writeInt(i);
        return this;
    }

    public PacketByteBufBuilder write(long l) {
        this.buf.writeLong(l);
        return this;
    }

    public PacketByteBufBuilder write(char c) {
        this.buf.writeChar(c);
        return this;
    }

    public PacketByteBufBuilder write(byte b) {
        this.buf.writeByte(b);
        return this;
    }

    public PacketByteBufBuilder var(int i) {
        this.buf.writeVarInt(i);
        return this;
    }

    public PacketByteBufBuilder var(long l) {
        this.buf.writeVarLong(l);
        return this;
    }

    public void send() {
        this.sender.send();
    }
}
