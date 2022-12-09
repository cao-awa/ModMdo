package com.github.cao.awa.modmdo.attack.ddos.recorder;

import org.apache.logging.log4j.*;

import java.util.*;
import java.util.concurrent.*;

public class DdosAttackRecorder {
    public static final Logger LOGGER = LogManager.getLogger("DDOS Attack Recorder");
    private final long startStamp;
    private final Map<Integer, Long> attacks = new ConcurrentHashMap<>();
    private long endStamp;
    private long occurring = 0;
    private int times = 0;
    private long all;

    public DdosAttackRecorder(long startStamp) {
        this.startStamp = startStamp;
    }

    public long getStartStamp() {
        return startStamp;
    }

    public long getEndStamp() {
        return endStamp;
    }

    public int getTimes() {
        return times;
    }

    public Map<Integer, Long> getAttacks() {
        return attacks;
    }

    public void occurs() {
        this.occurring++;
    }

    public void occursAhead() {
        record(this.occurring);
        this.occurring = 0;
    }

    public void record(long occurs) {
        this.all += occurs;
        this.attacks.put(
                ++ times,
                all
        );
    }

    public long average() {
        return all / Math.max(1, times);
    }

    public long getOccurring() {
        return occurring;
    }
}
