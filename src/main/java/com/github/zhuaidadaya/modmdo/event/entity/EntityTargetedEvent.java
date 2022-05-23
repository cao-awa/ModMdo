package com.github.zhuaidadaya.modmdo.event.entity;

import com.github.zhuaidadaya.modmdo.event.*;
import net.minecraft.entity.*;

public abstract class EntityTargetedEvent<T extends ModMdoEvent<?>> extends ModMdoEvent<T> {
    public abstract Entity getTargeted();
}
