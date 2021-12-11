package com.github.zhuaidadaya.modMdo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Test {
    public static void main(String[] args) {
        String test = "${java:os}";

        System.setProperty("log4j2.formatMsgNoLookups", "true");

        Logger logger = LogManager.getLogger("test");

        logger.error("test: {}", test);
    }
}
