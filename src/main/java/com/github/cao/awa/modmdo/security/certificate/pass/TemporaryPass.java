package com.github.cao.awa.modmdo.security.certificate.pass;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.security.certificate.*;

@Server
public class TemporaryPass extends Pass {
    private String organizer;
    private long time;

    public static TemporaryPass empty() {
        return new TemporaryPass();
    }

    public String getOrganizer() {
        return this.organizer;
    }

    public TemporaryPass setOrganizer(String organizer) {
        this.organizer = organizer;
        return this;
    }

    public long getTime() {
        return this.time;
    }

    public TemporaryPass setTime(long time) {
        this.time = time;
        return this;
    }

    public String formatRemaining() {
        return TemporaryCertificate.remaining(
                this.time,
                0
        );
    }
}
