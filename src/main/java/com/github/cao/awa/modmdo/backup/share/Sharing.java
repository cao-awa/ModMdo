package com.github.cao.awa.modmdo.backup.share;

import com.github.cao.awa.modmdo.storage.*;
import it.unimi.dsi.fastutil.objects.*;

public abstract class Sharing extends Storable {
    public abstract Object2ObjectOpenHashMap<String, String> detect();

    public abstract void affect(Object2ObjectOpenHashMap<String, String> diffs);

    public abstract void action();

    public abstract void offline();
}
