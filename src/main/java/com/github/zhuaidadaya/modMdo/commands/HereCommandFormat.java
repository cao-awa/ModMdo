package com.github.zhuaidadaya.modMdo.commands;

import com.github.zhuaidadaya.modMdo.simple.vec.XYZ;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public interface HereCommandFormat {
    TranslatableText formatHereTip(String dimension, XYZ xyz, ServerPlayerEntity player, ServerPlayerEntity whoUseHere);
    TranslatableText formatHereFeedBack(ServerPlayerEntity player);
    TranslatableText formatHereFailedFeedBack(ServerPlayerEntity player);
    TranslatableText formatHereDisabled();
}
