package com.github.cao.awa.modmdo.resource.loader;

import java.io.File;
import java.io.InputStream;

public class ResourcesLoader {
    public static InputStream getResource(String resource) {
        return ResourcesLoader.class.getClassLoader().getResourceAsStream(resource);
    }

    public static File getResourceByFile(String resource) {
        return new File(String.valueOf(ResourcesLoader.class.getResource(resource)));
    }
}
