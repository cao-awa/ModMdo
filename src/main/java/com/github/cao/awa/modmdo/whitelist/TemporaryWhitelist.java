package com.github.cao.awa.modmdo.whitelist;

import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.utils.times.*;
import org.json.*;

public final class TemporaryWhitelist extends Whitelist {
    private final String name;
    private final long recording;
    private final long millions;

    public TemporaryWhitelist(String name, long recording, long millions) {
        super(name, new LoginRecorde(name, null, LoginRecordeType.TEMPORARY));
        this.name = name;
        this.recording = recording;
        this.millions = millions;
    }

    public boolean isValid() {
        return TimeUtil.processMillion(recording) < millions;
    }

    public String name() {
        return name;
    }

    public long recording() {
        return recording;
    }

    public long millions() {
        return millions;
    }

    @Override
    public JSONObject toJSONObject() {
        return new JSONObject();
    }
}
