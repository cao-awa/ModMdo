package com.github.zhuaidadaya.modmdo.commands;

import com.github.zhuaidadaya.modmdo.simple.vec.XYZ;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public interface HereCommandFormat {
    TranslatableText formatHereTip(String dimension, XYZ xyz, ServerPlayerEntity player, ServerPlayerEntity whoUseHere);
    TranslatableText formatHereFeedBack(ServerPlayerEntity player);
    TranslatableText formatHereFailedFeedBack(ServerPlayerEntity player);
    TranslatableText formatHereDisabled();
}
