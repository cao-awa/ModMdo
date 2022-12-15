package com.github.cao.awa.modmdo.security.certificate;

import com.alibaba.fastjson2.*;
import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.security.certificate.pass.*;
import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.jetbrains.annotations.*;

import java.util.*;

@Server
public final class TemporaryCertificate extends Certificate {
    private final long recording;
    private long millions;
    private TemporaryCertificate spare;
    private TemporaryPass pass;

    public TemporaryCertificate(@NotNull String name, long recording, long millions) {
        super(
                name,
                new LoginRecorde(name,
                                 null,
                                 null,
                                 LoginRecordeType.TEMPORARY
                )
        );
        this.recording = recording;
        this.millions = millions;
    }

    public TemporaryCertificate(@NotNull String name, LoginRecorde recorde, long recording, long millions) {
        super(
                name,
                recorde
        );
        this.recording = recording;
        this.millions = millions;
    }

    public static TemporaryCertificate build(JSONObject json) {
        String uniqueId = EntrustEnvironment.trys(
                () -> json.getString("unique_id"),
                () -> ""
        );
        long recording = json.getLong("recording");
        long millions = json.getLong("millions");
        return new TemporaryCertificate(
                json.getString("name"),
                new LoginRecorde(uniqueId,
                                 EntrustEnvironment.trys(() -> UUID.fromString(json.getString("uuid"))),
                                 null,
                                 LoginRecordeType.TEMPORARY
                ),
                recording,
                millions
        );
    }

    public TemporaryPass getPass() {
        return this.pass;
    }

    public void setPass(TemporaryPass pass) {
        this.pass = pass;
    }

    public TemporaryCertificate snapSpare() {
        return new TemporaryCertificate(
                this.getName(),
                TimeUtil.millions(),
                this.spare.getMillions()
        );
    }
    
    public TemporaryCertificate getSpare() {
        return this.spare;
    }

    public void setSpare(TemporaryCertificate spare) {
        this.spare = spare;
    }

    public long getMillions() {
        return this.millions;
    }

    public void setMillions(long millions) {
        this.millions = millions;
    }

    public boolean isValid() {
        return calculateMillions() < this.millions;
    }

    public long calculateMillions() {
        return TimeUtil.processMillion(this.recording);
    }

    public long getRecording() {
        return this.recording;
    }

    public String formatRemaining() {
        return remaining(
                getMillions(),
                calculateMillions()
        );
    }

    public static String remaining(long millions, long calculate) {
        long calculated = Math.abs(millions - calculate);
        long second;
        if ((second = TimeUtil.formatSecond(calculated)) > 59) {
            long minute;
            if ((minute = TimeUtil.formatMinute(calculated)) > 59) {
                long hour;
                if ((hour = TimeUtil.formatHour(calculated)) > 23) {
                    long day = TimeUtil.formatDay(calculated);
                    return day + "d, " + TimeUtil.processRemainingHours(calculated) + "h, " + TimeUtil.processRemainingMinutes(calculated) + "m, " + TimeUtil.processRemainingSeconds(calculated) + "s";
                }
                return hour + "h, " + TimeUtil.processRemainingMinutes(calculated) + "m, " + TimeUtil.processRemainingSeconds(calculated) + "s";
            }
            return minute + "m, " + TimeUtil.processRemainingSeconds(calculated) + "s";
        }
        return second + "s";
    }

    public TemporaryCertificate setType(String type) {
        super.setType(type);
        return this;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put(
                "type",
                "temporary"
        );
        json.put(
                "recording",
                this.recording
        );
        json.put(
                "millions",
                this.millions
        );
        json.put(
                "uuid",
                this.getRecorde().getUuid()
        );
        json.put(
                "unique_id",
                this.getRecorde().getUniqueId()
        );
        json.put(
                "name",
                this.getName()
        );
        return json;
    }
}
