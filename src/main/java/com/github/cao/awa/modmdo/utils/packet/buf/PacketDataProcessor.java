package com.github.cao.awa.modmdo.utils.packet.buf;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import org.json.*;

public class PacketDataProcessor {
    private final PacketByteBuf buf;

    public PacketDataProcessor(PacketByteBuf buf) {
        this.buf = buf;
    }

    public JSONObject readJSONObject() {
        return new JSONObject(EntrustEnvironment.get(
                this.buf::readString,
                ""
        ));
    }

    public Identifier readIdentifier() {
        return new Identifier(EntrustEnvironment.get(
                this.buf::readString,
                ""
        ));
    }

    public int readInt() {
        return EntrustEnvironment.get(
                this.buf::readInt,
                - 1
        );
    }

    public long readLong() {
        return EntrustEnvironment.get(
                this.buf::readLong,
                - 1L
        );
    }

    public String readString() {
        return EntrustEnvironment.get(
                this.buf::readString,
                ""
        );
    }


}
