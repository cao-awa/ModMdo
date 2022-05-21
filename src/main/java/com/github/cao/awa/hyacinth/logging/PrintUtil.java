package com.github.cao.awa.hyacinth.logging;

import com.github.cao.awa.hyacinth.logging.track.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.apache.logging.log4j.*;

import java.util.*;

public class PrintUtil {
    private static final Logger TRACKER = LogManager.getLogger("Hyacinth:Tracker");
    public static boolean debugging = false;

    @SafeVarargs
    public static <T> void printsln(T... messages) {
        for (T target : messages) {
            System.out.println(target);
        }
    }

    public static <T> void info(Logger logger, T message) {
        logger.info(message);
    }

    public static <T> void info(Logger logger, T[] messages, int limit) {
        int printed = 0;
        for (T message : messages) {
            if (printed++ == limit) {
                break;
            }
            logger.info(message);
        }
    }

    public static <T> void debug(Logger logger, T[] messages) {
        for (T message : messages) {
            logger.debug(message);
        }
    }

    public static <T> void debug(Logger logger, T[] messages, int limit) {
        int printed = 0;
        for (T message : messages) {
            if (printed++ == limit) {
                break;
            }
            logger.debug(message);
        }
    }

    public static <T> void info(Logger logger, Collection<T> messages) {
        for (T message : messages) {
            logger.info(message);
        }
    }

    public static <T> void info(Logger logger, Collection<T> messages, int limit) {
        int printed = 0;
        for (T message : messages) {
            if (printed++ == limit) {
                break;
            }
            logger.info(message);
        }
    }

    public static <T> void debug(Logger logger, Collection<T> messages) {
        for (T message : messages) {
            logger.debug(message);
        }
    }

    public static <T> void debug(Logger logger, Collection<T> messages, int limit) {
        int printed = 0;
        for (T message : messages) {
            if (printed++ == limit) {
                break;
            }
            logger.debug(message);
        }
    }

    public static Tracking tacker(StackTraceElement[] tracker) {
        return new Tracking(tracker, 5, 1, "Tracking...");
    }

    public static Tracking tacker(String... messages) {
        return new Tracking(Thread.currentThread().getStackTrace(), 5, 2, messages);
    }

    public static Tracking tacker(StackTraceElement[] tracker, String... messages) {
        return new Tracking(tracker, 5, 1, messages);
    }

    public static Tracking tacker(StackTraceElement[] tracker, int trackLimit, String... messages) {
        return new Tracking(tracker, trackLimit, 1, messages);
    }

    public static Tracking tacker(StackTraceElement[] tracker, int trackLimit, int startFrom, String... messages) {
        return new Tracking(tracker, trackLimit, startFrom, messages);
    }

    public static void messageToTracker(Tracking tracking) {
        if (debugging) {
            int limit = tracking.getTrackLimit();
            PrintUtil.info(TRACKER, "--Hyacinth Tracking: ");
            for (String message : tracking.getMessages()) {
                TRACKER.info("      " + message);
            }
            PrintUtil.info(TRACKER, "      --Hyacinth Tracking(Thread Traces): ");
            for (int i = tracking.getStartFrom(), trackerLength = tracking.getTracker().length; i < trackerLength; i++) {
                StackTraceElement elements = tracking.getTracker()[i];
                if (limit-- == 0) {
                    break;
                }
                TRACKER.info("         " + elements);
            }

            EntrustExecution.tryTemporary(tracking.pause());
        }
    }

    public static <T> void info(Logger logger, T[] messages) {
        for (T message : messages) {
            logger.info(message);
        }
    }

    public static <T> void debug(Logger logger, T message) {
        logger.debug(message);
    }
}
