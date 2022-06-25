package com.github.cao.awa.modmdo.develop.clazz;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.receptacle.*;
import it.unimi.dsi.fastutil.objects.*;

import java.io.*;
import java.lang.annotation.*;

public class ClazzScanner {
    private final ObjectArrayList<Class<?>> classes;

    public ClazzScanner(Class<?> clazz) {
        classes = getAllAssignedClass(clazz);
    }

    public ObjectArrayList<Class<?>> getTypesAnnotatedWith(Class<? extends Annotation> clazz) {
        ObjectArrayList<Class<?>> list = new ObjectArrayList<>();
        EntrustExecution.tryFor(classes, c -> {
            if (c.isAnnotationPresent(clazz)) {
                list.add(c);
            }
        });
        return list;
    }

    public ObjectArrayList<Class<?>> getAllAssignedClass(Class<?> clazz) {
        ObjectArrayList<Class<?>> classes = new ObjectArrayList<>();
        EntrustExecution.tryFor(getClasses(clazz), c -> {
            if (clazz.isAssignableFrom(c) && ! clazz.equals(c)) {
                classes.add(c);
            }
        });
        return classes;
    }

    public ObjectArrayList<Class<?>> getClasses(Class<?> clazz) {
        String pkg = clazz.getPackage().getName();
        Receptacle<ObjectArrayList<Class<?>>> result = new Receptacle<>(null);
        EntrustExecution.notNull(Thread.currentThread().getContextClassLoader().getResource(pkg.replace('.', '/')), resource -> {
            result.set(getClasses(new File(resource.getFile()), pkg));
        });
        return result.get();
    }

    private ObjectArrayList<Class<?>> getClasses(File dir, String path) {
        ObjectArrayList<Class<?>> classes = new ObjectArrayList<>();
        if (dir.exists()) {
            EntrustExecution.tryFor(dir.listFiles(), file -> {
                if (file.isDirectory()) {
                    classes.addAll(getClasses(file, path + "." + file.getName()));
                }
                String name = file.getName();
                if (name.endsWith(".class")) {
                    classes.add(Class.forName(path + "." + name.substring(0, name.length() - 6)));
                }
            });
        }
        return classes;
    }

    public ObjectArrayList<Class<?>> getClasses() {
        return classes;
    }
}
