package com.github.cao.awa.modmdo.mixins.oshi;

import org.slf4j.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import oshi.util.*;

@Mixin(FileUtil.class)
public class FileUtilMixin {
    @Redirect(method = "readPropertiesFromClassLoader", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"))
    private static void re(Logger instance, String s, Object o) {

    }
}
