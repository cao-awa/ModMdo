package com.github.zhuaidadaya.modmdo.event.entity;

import com.github.zhuaidadaya.modmdo.event.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.entity.*;

import java.util.*;

public abstract class EntityTargetedEvent<T extends ModMdoEvent<?>> extends ModMdoEvent<T> {
    public abstract ObjectArrayList<? extends Entity> getTargeted();
}
