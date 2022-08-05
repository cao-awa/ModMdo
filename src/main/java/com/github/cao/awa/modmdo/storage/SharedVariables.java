package com.github.cao.awa.modmdo.storage;

import com.github.cao.awa.hyacinth.logging.*;
import com.github.cao.awa.modmdo.security.key.*;
import com.github.zhuaidadaya.rikaishinikui.handler.config.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;

public class SharedVariables {
    public static final Logger LOGGER = LogManager.getLogger("ModMdoAuth");
    public static final String VERSION_ID = "1.0.40";
    public static final String SUFFIX = "-ES";
    public static final String MODMDO_VERSION_NAME = VERSION_ID + SUFFIX;
    public static final byte[] NONCE = "MODMDO:SERVER_NONCE_!+[RD]".getBytes();
    public static final int MODMDO_VERSION = 31;
    public static final Identifier CHECKING_CHANNEL = new Identifier("modmdo:check");
    public static final Identifier LOGIN_CHANNEL = new Identifier("modmdo:login");
    public static final Identifier CLIENT_CHANNEL = new Identifier("modmdo:client");
    public static final GlobalTracker TRACKER = new GlobalTracker();
    public static final SecureKeys SECURE_KEYS = new SecureKeys();
    public static String entrust = "ModMdo";
    public static boolean debug = false;
    public static DiskObjectConfigUtil staticConfig;
}
