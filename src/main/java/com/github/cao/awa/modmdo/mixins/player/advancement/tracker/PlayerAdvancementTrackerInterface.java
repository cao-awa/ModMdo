package com.github.cao.awa.modmdo.mixins.player.advancement.tracker;

import net.minecraft.advancement.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

import java.util.*;

@Mixin(PlayerAdvancementTracker.class)
public interface PlayerAdvancementTrackerInterface {
    @Accessor("advancementToProgress")
    static void setAdvancementToProgress(Map<Advancement, AdvancementProgress> advancementToProgress) {
        throw new AssertionError();
    }

    @Accessor("visibleAdvancements")
    static void setVisibleAdvancements(Set<Advancement> visibleAdvancements) {
        throw new AssertionError();
    }

    @Accessor("visibilityUpdates")
    static void setVisibilityUpdates(Set<Advancement> visibilityUpdates) {
        throw new AssertionError();
    }

    @Accessor("progressUpdates")
    static void setProgressUpdates(Set<Advancement> progressUpdates) {
        throw new AssertionError();
    }

    default void transfer() {

    }
}
