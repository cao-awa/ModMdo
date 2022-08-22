package com.github.cao.awa.modmdo.mixins.player.advancement.tracker;

import net.minecraft.advancement.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

import java.util.*;

@Mixin(PlayerAdvancementTracker.class)
public interface PlayerAdvancementTrackerInterface {
    @Mutable
    @Accessor("advancementToProgress")
    void setAdvancementToProgress(Map<Advancement, AdvancementProgress> advancementToProgress);

    @Mutable
    @Accessor("visibleAdvancements")
    void setVisibleAdvancements(Set<Advancement> visibleAdvancements);

    @Mutable
    @Accessor("visibilityUpdates")
    void setVisibilityUpdates(Set<Advancement> visibilityUpdates);

    @Mutable
    @Accessor("progressUpdates")
    void setProgressUpdates(Set<Advancement> progressUpdates);
}
