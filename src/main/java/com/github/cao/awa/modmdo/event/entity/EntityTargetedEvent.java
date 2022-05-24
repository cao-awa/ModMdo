package com.github.cao.awa.modmdo.event.entity;

import com.github.cao.awa.modmdo.event.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;

public abstract class EntityTargetedEvent<T extends ModMdoEvent<?>> extends ModMdoEvent<T> {
    public abstract ObjectArrayList<? extends Entity> getTargeted();
}
