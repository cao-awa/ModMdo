package com.github.cao.awa.hyacinth.logging.track;

import com.github.cao.awa.hyacinth.logging.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;
import org.apache.logging.log4j.*;

import java.text.*;
import java.util.*;

public class Tracking {
    public static final Logger TRACKER = LogManager.getLogger("Hyacinth:Tracker");
    private static final Calendar calendar = Calendar.getInstance();
    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss" );
    private Thread parent;
    private StackTraceElement[] tracker;
    private String[] messages;
    private int startFrom;
    private int trackLimit;
    private long pause;

    public Tracking(String... messages) {
        this.tracker = Thread.currentThread().getStackTrace();
        this.messages = messages;
        this.trackLimit = 5;
        this.startFrom = 2;
        pause = 0;
    }

    public Tracking(StackTraceElement[] tracker, String... messages) {
        this.tracker = tracker;
        this.messages = messages;
        this.trackLimit = 5;
        this.startFrom = 1;
        pause = 0;
    }

    public Tracking(StackTraceElement[] tracker, int trackLimit, String... messages) {
        this.tracker = tracker;
        this.messages = messages;
        this.trackLimit = trackLimit;
        this.startFrom = 1;
        pause = 0;
    }

    public Tracking(StackTraceElement[] tracker, int trackLimit, int startFrom, String... messages) {
        this.tracker = tracker;
        this.messages = messages;
        this.trackLimit = trackLimit;
        this.startFrom = startFrom;
        pause = 0;
    }

    public Tracking(StackTraceElement[] tracker, int trackLimit, int startFrom, long pause, String... messages) {
        this.tracker = tracker;
        this.messages = messages;
        this.trackLimit = trackLimit;
        this.startFrom = startFrom;
        this.pause = pause;
    }

    public Tracking(Thread parent, StackTraceElement[] tracker, int trackLimit, int startFrom , String... messages) {
        this.tracker = tracker;
        this.messages = messages;
        this.trackLimit = trackLimit;
        this.startFrom = startFrom;
        this.parent = parent;
    }

    public Tracking(Thread parent, StackTraceElement[] tracker, int trackLimit, int startFrom, long pause, String... messages) {
        this.tracker = tracker;
        this.messages = messages;
        this.trackLimit = trackLimit;
        this.startFrom = startFrom;
        this.pause = pause;
        this.parent = parent;
    }

    public long getPause() {
        return pause;
    }

    public Tracking setPause(long pause) {
        this.pause = pause;
        return this;
    }

    public StackTraceElement[] getTracker() {
        return tracker;
    }

    public Tracking setTracker(StackTraceElement[] tracker) {
        this.tracker = tracker;
        return this;
    }

    public String[] getMessages() {
        return messages;
    }

    public Tracking setMessages(String[] messages) {
        this.messages = messages;
        return this;
    }

    public int getStartFrom() {
        return startFrom;
    }

    public Tracking setStartFrom(int startFrom) {
        this.startFrom = startFrom;
        return this;
    }

    public int getTrackLimit() {
        return trackLimit;
    }

    public Tracking setTrackLimit(int trackLimit) {
        this.trackLimit = trackLimit;
        return this;
    }

    public ExceptingTemporary pause() {
        return () -> {
            Thread.sleep(pause);
            EntrustExecution.tryAssertNotNull(parent, thread -> thread.join(pause));
        };
    }

    public ExceptingTemporary pause(boolean parent) {
        return () -> {
            Thread.sleep(pause);
            if (parent) {
                EntrustExecution.tryAssertNotNull(this.parent, thread -> thread.join(pause));
            }
        };
    }

    public void print() {
        int limit = getTrackLimit();
        PrintUtil.info(TRACKER, "--Hyacinth Tracking: ");
        for (String message : getMessages()) {
            TRACKER.debug("      " + message);
        }
        PrintUtil.info(TRACKER, "      --Hyacinth Tracking(Thread Traces): ");
        for (int i = getStartFrom(), trackerLength = getTracker().length; i < trackerLength; i++) {
            StackTraceElement elements = getTracker()[i];
            if (limit-- == 0) {
                break;
            }
            TRACKER.debug("         " + elements);
        }
    }

    public Thread getParent() {
        return parent;
    }

    public StackTraceElement[] getParentTracker() {
        return parent.getStackTrace();
    }

    public String shortPrint() {
        StringBuilder builder = new StringBuilder();
        int limit = getTrackLimit();
        TRACKER.info("--Hyacinth Tracking: ");
        for (String message : getMessages()) {
            TRACKER.info("    " + message);
            builder.append(String.format("[%s]", formatter.format(calendar.getTime()))).append(" ").append(message).append("\n");
        }
        for (int i = getStartFrom(), trackerLength = getTracker().length; i < trackerLength; i++) {
            StackTraceElement elements = getTracker()[i];
            if (limit-- == 0) {
                break;
            }
            builder.append(String.format("[%s]", formatter.format(calendar.getTime()))).append("     ").append(elements.toString()).append("\n");
        }
        builder.append(String.format("[%s]", formatter.format(calendar.getTime()))).append(" ").append("Cause by: ").append("\n");
        for (int i = getStartFrom(), trackerLength = getParentTracker().length; i < trackerLength; i++) {
            StackTraceElement elements = getParentTracker()[i];
            if (limit-- == 0) {
                break;
            }
            builder.append(String.format("[%s]", formatter.format(calendar.getTime()))).append("     ").append(elements.toString()).append("\n");
        }
        return builder.toString();
    }
}
