package com.github.cao.awa.modmdo.storage;

import com.github.cao.awa.modmdo.config.*;
import com.github.cao.awa.modmdo.security.key.*;
import net.minecraft.util.*;
import org.apache.logging.log4j.*;

public class SharedVariables {
    public static final String VERSION_ID = "1.0.42";
    public static final String SUFFIX = "-Auth";
    public static final String MODMDO_VERSION_NAME = VERSION_ID + SUFFIX;
    public static final byte[] NONCE = "MODMDO:SERVER_NONCE_!+[RD]".getBytes();
    public static final byte[] MODMDO_NONCE_HEAD = "MODMDO:SERVER_NONCE_!+".getBytes();
    public static final Identifier CHECKING_CHANNEL = new Identifier("modmdo:check");
    public static final Identifier LOGIN_CHANNEL = new Identifier("modmdo:login");
    public static final Identifier CLIENT_CHANNEL = new Identifier("modmdo:client");
    public static final Identifier SERVER_CHANNEL = new Identifier("modmdo:server");
    public static final Logger LOGGER = LogManager.getLogger("ModMdoAuth");
    public static final SecureKeys SECURE_KEYS = new SecureKeys();
    public static String entrust = "ModMdo";
    public static DiskConfigUtil staticConfig;
}
