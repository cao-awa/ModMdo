package com.github.cao.awa.modmdo.mixins.oshi;

import org.spongepowered.asm.mixin.*;
import oshi.util.*;

@Mixin(FileUtil.class)
public class FileUtilMixin {
    // Only develop
//    @Redirect(method = "readPropertiesFromClassLoader", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"))
//    private static void re(Logger instance, String s, Object o) {
//
//    }
}
