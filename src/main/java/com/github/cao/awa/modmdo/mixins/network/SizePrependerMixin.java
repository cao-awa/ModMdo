package com.github.cao.awa.modmdo.mixins.network;

import io.netty.buffer.*;
import io.netty.channel.*;
import net.minecraft.network.*;
import org.spongepowered.asm.mixin.*;

@Mixin(SizePrepender.class)
public class SizePrependerMixin {
    /**
     * @author
     */
    @Overwrite
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int i = byteBuf.readableBytes();
        int j = PacketByteBuf.getVarIntLength(i);
        PacketByteBuf packetByteBuf = new PacketByteBuf(byteBuf2);
        packetByteBuf.ensureWritable(j + i);
        packetByteBuf.writeVarInt(i);
        packetByteBuf.writeBytes(byteBuf, byteBuf.readerIndex(), i);
    }
}
