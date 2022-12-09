package com.github.cao.awa.hyacinth.logging;

import com.github.cao.awa.hyacinth.logging.track.*;
import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;

import java.io.*;

public class GlobalTracker {
    private BufferedWriter writer;

    public GlobalTracker() {
        EntrustEnvironment.trys(() -> {
            File file = new File("logs/tracker/latest.log");
            file.getParentFile()
                .mkdirs();
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
        });
    }

    public static Tracking tacker(StackTraceElement[] tracker) {
        return new Tracking(
                tracker,
                5,
                1,
                "Tracking..."
        );
    }

    public static Tracking tacker(String... messages) {
        return new Tracking(
                Thread.currentThread()
                      .getStackTrace(),
                5,
                2,
                messages
        );
    }

    public static Tracking tacker(StackTraceElement[] tracker, String... messages) {
        return new Tracking(
                tracker,
                5,
                1,
                messages
        );
    }

    public static Tracking tacker(StackTraceElement[] tracker, int trackLimit, String... messages) {
        return new Tracking(
                tracker,
                trackLimit,
                1,
                messages
        );
    }

    public void info(String message) {
        Tracking.TRACKER.info(message);
    }

    public void err(String message, Throwable throwable) {
        Tracking.TRACKER.error(
                message,
                throwable
        );
    }

    public void warn(String message) {
        Tracking.TRACKER.warn(message);
    }

    public void debug(String message) {
        submit(message);
    }

    public void submit(String message) {
        if (SharedVariables.debug) {
            String data = tacker(
                    Thread.currentThread()
                          .getStackTrace(),
                    - 1,
                    2,
                    message
            ).shortPrint();
            EntrustEnvironment.trys(() -> {
                if (writer != null) {
                    writer.write(data);
                    writer.flush();
                }
            });
        }
    }

    public static Tracking tacker(StackTraceElement[] tracker, int trackLimit, int startFrom, String... messages) {
        return new Tracking(
                tracker,
                trackLimit,
                startFrom,
                messages
        );
    }

    public void submit(String message, Temporary... actons) {
        for (Temporary temporary : actons) {
            temporary.apply();
            submit(message);
        }
    }

    public void submit(String message, Throwable throwable) {
        if (SharedVariables.debug) {
            String data = tacker(
                    Thread.currentThread(),
                    throwable,
                    - 1,
                    2,
                    message
            ).shortPrint();
            EntrustEnvironment.trys(() -> {
                if (writer != null) {
                    writer.write(data);
                    writer.flush();
                }
            });
        }
    }

    public static Tracking tacker(Thread parent, Throwable tracker, int trackLimit, int startFrom, String... messages) {
        return new Tracking(
                parent,
                tracker,
                trackLimit,
                startFrom,
                messages
        );
    }

    public void submit(Thread parent, String message, Temporary... actons) {
        for (Temporary temporary : actons) {
            temporary.apply();
            submit(
                    parent,
                    message
            );
        }
    }

    public void submit(Thread parent, String message) {
        if (SharedVariables.debug) {
            String data = tacker(
                    parent,
                    Thread.currentThread()
                          .getStackTrace(),
                    - 1,
                    2,
                    message
            ).shortPrint();
            EntrustEnvironment.trys(() -> {
                if (writer != null) {
                    writer.write(data);
                    writer.flush();
                }
            });
        }
    }

    public static Tracking tacker(Thread parent, StackTraceElement[] tracker, int trackLimit, int startFrom, String... messages) {
        return new Tracking(
                parent,
                tracker,
                trackLimit,
                startFrom,
                messages
        );
    }

    public void submit(Thread parent, String message, Throwable throwable) {
        if (SharedVariables.debug) {
            String data = tacker(
                    parent,
                    throwable.getStackTrace(),
                    - 1,
                    2,
                    message
            ).shortPrint();
            EntrustEnvironment.trys(() -> {
                if (writer != null) {
                    writer.write(data);
                    writer.flush();
                }
            });
        }
    }
}
