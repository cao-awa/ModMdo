package com.github.zhuaidadaya.MCH.utils.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * note The Config element
 */
public @interface ConfigElements {
    /**
     * <code>element</code> Are Element Name in This
     * <p>
     * Default Value <code>empty string</code>
     */
    @NotNull String element() default "";

    /**
     * <code>note</code> Are Element Note in This
     * <p>
     * Default Value <code>empty string</code>
     */
    @Nullable String note() default "";
}