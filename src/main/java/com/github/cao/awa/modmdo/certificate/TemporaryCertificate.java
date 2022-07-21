package com.github.cao.awa.modmdo.certificate;

import com.github.cao.awa.modmdo.annotations.platform.*;
import com.github.cao.awa.modmdo.certificate.pass.*;
import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.*;

import java.util.*;

@Server
public final class TemporaryCertificate extends Certificate {
    private final long recording;
    private long millions;
    private TemporaryCertificate spare;
    private TemporaryPass pass;

    public TemporaryCertificate(String name, long recording, long millions) {
        super(name, new LoginRecorde(name, null,null, LoginRecordeType.TEMPORARY));
        this.recording = recording;
        this.millions = millions;
    }

    public TemporaryCertificate(String name, LoginRecorde recorde, long recording, long millions) {
        super(name, recorde);
        this.recording = recording;
        this.millions = millions;
    }

    public static TemporaryCertificate build(JSONObject json) {
        UUID uuid = EntrustParser.trying(() -> UUID.fromString(json.getString("uuid")));
        String uniqueId = EntrustParser.trying(() -> json.getString("unique_id"), () -> "");
        long recording = json.getLong("recording");
        long millions = json.getLong("millions");
        return new TemporaryCertificate(json.getString("name"), new LoginRecorde(uniqueId, uuid,null, LoginRecordeType.TEMPORARY), recording, millions);
    }

    public TemporaryPass getPass() {
        return pass;
    }

    public void setPass(TemporaryPass pass) {
        this.pass = pass;
    }

    public TemporaryCertificate getSpare() {
        return new TemporaryCertificate(getName(), TimeUtil.millions(), spare.getMillions());
    }

    public void setSpare(TemporaryCertificate spare) {
        this.spare = spare;
    }

    public long getMillions() {
        return millions;
    }

    public void setMillions(long millions) {
        this.millions = millions;
    }

    public boolean isValid() {
        return calculateMillions() < millions;
    }

    public boolean notValid() {
        return calculateMillions() > millions;
    }

    public long calculateMillions() {
        return TimeUtil.processMillion(recording);
    }

    public long getRecording() {
        return recording;
    }

    public String formatRemaining() {
        return remaining(getMillions(), calculateMillions());
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
        json.put("type", "temporary");
        json.put("recording", recording);
        json.put("millions", millions);
        json.put("uuid", getRecorde().getUuid());
        json.put("unique_id", getRecorde().getUniqueId());
        json.put("name", getName());
        return json;
    }
}
