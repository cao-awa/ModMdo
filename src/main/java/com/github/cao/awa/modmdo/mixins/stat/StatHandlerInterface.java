package com.github.cao.awa.modmdo.mixins.stat;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.stat.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(StatHandler.class)
public interface StatHandlerInterface {
    @Accessor
    Object2IntMap<Stat<?>> getStatMap();
}
