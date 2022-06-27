package com.github.cao.awa.modmdo.certificate.pass;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.certificate.*;

@Server
public class TemporaryPass extends Pass {
    private String organizer;
    private long time;

    public static TemporaryPass empty() {
        return new TemporaryPass();
    }

    public String getOrganizer() {
        return organizer;
    }

    public TemporaryPass setOrganizer(String organizer) {
        this.organizer = organizer;
        return this;
    }

    public long getTime() {
        return time;
    }

    public TemporaryPass setTime(long time) {
        this.time = time;
        return this;
    }

    public String formatRemaining() {
        return TemporaryCertificate.remaining(time, 0);
    }
}
