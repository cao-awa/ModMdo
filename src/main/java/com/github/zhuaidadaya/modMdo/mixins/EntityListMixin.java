package com.github.zhuaidadaya.modMdo.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.world.EntityList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

import static com.github.zhuaidadaya.modMdo.storage.Variables.enabledCancelEntitiesTIck;

@Mixin(EntityList.class)
public class EntityListMixin {

    /**
     * @author 草awa
     */
    @Inject(method = "forEach",at = @At("HEAD"), cancellable = true)
    public void forEach(Consumer<Entity> action, CallbackInfo ci) {
        if(enabledCancelEntitiesTIck) {
            ci.cancel();
        }
    }
}
