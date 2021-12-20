package com.github.zhuaidadaya.modMdo;

import java.util.LinkedHashMap;

public class Test {
    public static void main(String[] args) {
        LinkedHashMap<String, String> argMap = new LinkedHashMap<>();

        for(String s : args)
            argMap.put(s.split("=")[0], s.split("=")[1]);

        String name = argMap.get("blockName");
    }
}
