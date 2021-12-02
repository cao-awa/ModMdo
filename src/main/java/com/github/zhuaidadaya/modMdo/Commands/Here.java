package com.github.zhuaidadaya.modMdo.Commands;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Here {
    String formatHereTip(String dimension, XYZ xyz, ServerPlayerEntity player, DimensionTips dimensionTips,ServerPlayerEntity whoUseHere);
    String formatHereFeedBack(ServerPlayerEntity player);
    String formatHereFailedFeedBack(ServerPlayerEntity player);
}
