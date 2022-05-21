package com.github.cao.awa.hyacinth.logging.track;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;

public class Tracking {
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
        };
    }
}
