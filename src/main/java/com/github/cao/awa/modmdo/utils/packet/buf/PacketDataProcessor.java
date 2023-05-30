package com.github.cao.awa.modmdo.utils.packet.buf;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import com.alibaba.fastjson2.*;

public class PacketDataProcessor {
    private final PacketByteBuf buf;

    public PacketDataProcessor(PacketByteBuf buf) {
        this.buf = buf;
    }

    public JSONObject readJSONObject() {
        return JSONObject.parseObject(EntrustEnvironment.get(
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
