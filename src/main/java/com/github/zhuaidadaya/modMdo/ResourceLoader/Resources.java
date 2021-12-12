package com.github.zhuaidadaya.modMdo.resourceLoader;

import java.io.File;
import java.io.InputStream;

public class Resources {
    public static InputStream getResource(String resource, Class<?> getC) {
        return getC.getResourceAsStream(resource);
    }

    public static File getResourceByFile(String resource, Class<?> getC) {
        return new File(String.valueOf(getC.getResource(resource)));
    }
}
