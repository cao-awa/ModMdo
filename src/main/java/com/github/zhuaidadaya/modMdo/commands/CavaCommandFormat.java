package com.github.zhuaidadaya.modMdo.commands;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public interface CavaCommandFormat {
    TranslatableText formatCavaDeleted(String cavaID);
    TranslatableText formatCavaDeleteFail();
    TranslatableText formatCavaExists();
    TranslatableText formatCavaCreated(String cavaID);
    TranslatableText formatCavaCreateFail();
    TranslatableText formatNoCava();
    TranslatableText formatCavaTip(ServerPlayerEntity player);
    TranslatableText formatCavaDisabled();
}
