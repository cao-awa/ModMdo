package com.github.cao.awa.modmdo.annotations;

import java.lang.annotation.*;

/**
 * <p>Note a class are auto automatic</p>
 * <p>Can automatic or manual to use the class</p>
 *
 * <p>Use in class</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Auto {
}
