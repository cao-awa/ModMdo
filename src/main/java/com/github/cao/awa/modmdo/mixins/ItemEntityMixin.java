package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.storage.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import static com.github.cao.awa.modmdo.storage.SharedVariables.*;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow
    private int itemAge;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    /**
     * 草二号机取消了重写, 重做了方法
     *
     * @author 草awa
     * @author 草二号机
     *
     */
    @Inject(method = "tick",at = @At("RETURN"))
    public void tick(CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            if (age == - 1) {
                age = itemAge;
            }
            age++;
            if (itemAge % 5999 == 0) {
                itemAge = 0;
            }
            if (age > itemDespawnAge) {
                discard();
                age = - 1;
            }
        }
    }
}
