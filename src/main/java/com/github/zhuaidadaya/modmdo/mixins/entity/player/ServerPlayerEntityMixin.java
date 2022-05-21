package com.github.zhuaidadaya.modmdo.mixins.entity.player;

import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.server.network.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.zhuaidadaya.modmdo.storage.Variables.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
}
