package com.github.cao.awa.modmdo.resource.loader;

import java.io.File;
import java.io.InputStream;

public class ResourcesLoader {
    public static InputStream getResource(String resource, Class<?> getC) {
        return getC.getClassLoader().getResourceAsStream(resource);
    }

    public static File getResourceByFile(String resource, Class<?> getC) {
        return new File(String.valueOf(getC.getResource(resource)));
    }
}
