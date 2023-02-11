package com.github.cao.awa.modmdo.supplier;

import java.util.function.*;

/**
 * Suppliers factor.
 */
public final class SupplierTemplates {
    private static final Supplier<String> EMPTY_STRING = () -> "";

    /**
     * Create supplier return an empty string.
     *
     * @return String supplier
     */
    public static Supplier<String> emptyString() {
        return EMPTY_STRING;
    }

    /**
     * Create supplier return null.
     *
     * @return Null supplier
     */
    public static <T> Supplier<T> createNull() {
        return () -> null;
    }
}
