package com.github.zhuaidadaya.modmdo.mixins;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ArgumentTypes.class)
public class ArgumentTypesMixin {
    @Shadow @Final private static Map CLASS_MAP;

    @Shadow @Final private static Map ID_MAP;

    /**
     * @author 草awa
     */
    @Inject(method = "register(Ljava/lang/String;Ljava/lang/Class;Lnet/minecraft/command/argument/serialize/ArgumentSerializer;)V", at = @At("HEAD"))
    private static <T extends ArgumentType<?>> void register(String id, Class<T> class_, ArgumentSerializer<T> argumentSerializer, CallbackInfo ci) {
        Identifier identifier = new Identifier(id);
        CLASS_MAP.remove(class_);
        ID_MAP.remove(identifier);
    }
}
