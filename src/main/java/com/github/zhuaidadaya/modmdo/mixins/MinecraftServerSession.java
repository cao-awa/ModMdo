package com.github.zhuaidadaya.modmdo.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Reference <a herf="https://github.com/Szum123321/textile_backup/blob/2.x/src/main/java/net/szum123321/textile_backup/mixin/MinecraftServerSessionAccessor.java">TextileBackup</a>
 */
@Mixin(MinecraftServer.class)
public interface MinecraftServerSession {
    @Accessor
    LevelStorage.Session getSession();
}