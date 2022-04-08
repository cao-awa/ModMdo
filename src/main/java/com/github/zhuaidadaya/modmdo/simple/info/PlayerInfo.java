package com.github.zhuaidadaya.modmdo.simple.info;

import com.github.zhuaidadaya.modmdo.simple.vec.RXY;
import com.github.zhuaidadaya.modmdo.simple.vec.XYZ;
import com.github.zhuaidadaya.modmdo.utils.dimension.DimensionUtil;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerInfo {
    private final XYZ xyz;
    private final RXY rxy;
    private final String dimension;

    public PlayerInfo(ServerPlayerEntity player) {
        xyz = new XYZ(player.getPos());
        rxy = new RXY(player.getRotationClient());
        dimension = DimensionUtil.getDimension(player);
    }

    public XYZ getXyz() {
        return xyz;
    }

    public RXY getRxy() {
        return rxy;
    }

    public String getDimension() {
        return dimension;
    }
}
