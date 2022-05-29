package com.github.cao.awa.modmdo.mixins;

import com.github.cao.awa.modmdo.storage.*;
import com.mojang.brigadier.arguments.*;
import net.minecraft.command.argument.*;
import net.minecraft.command.argument.serialize.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(ArgumentTypes.class)
public class ArgumentTypesMixin {
    @Shadow @Final private static Map CLASS_MAP;

    @Shadow @Final private static Map ID_MAP;

    /**
     * @author 草awa
     */
    @Inject(method = "register(Ljava/lang/String;Ljava/lang/Class;Lnet/minecraft/command/argument/serialize/ArgumentSerializer;)V", at = @At("HEAD"))
    private static <T extends ArgumentType<?>> void register(String id, Class<T> class_, ArgumentSerializer<T> argumentSerializer, CallbackInfo ci) {
        if (SharedVariables.isActive()) {
            Identifier identifier = new Identifier(id);
            CLASS_MAP.remove(class_);
            ID_MAP.remove(identifier);
        }
    }
}
