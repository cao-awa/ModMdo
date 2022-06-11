package com.github.cao.awa.modmdo.certificate;

import com.github.cao.awa.modmdo.server.login.*;
import com.github.cao.awa.modmdo.utils.times.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import org.json.*;

import java.util.*;

public final class TemporaryCertificate extends Certificate {
    private final long recording;
    private long millions;

    public TemporaryCertificate(String name, long recording, long millions) {
        super(name, new LoginRecorde(name, null, LoginRecordeType.TEMPORARY));
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
        return new TemporaryCertificate(json.getString("name"), new LoginRecorde(uniqueId, uuid, LoginRecordeType.TEMPORARY), recording, millions);
    }

    public boolean isValid() {
        return calculateMillions() < millions;
    }

    public long calculateMillions() {
        return TimeUtil.processMillion(recording);
    }

    public long getRecording() {
        return recording;
    }

    public long getMillions() {
        return millions;
    }

    public void setMillions(long millions) {
        this.millions = millions;
    }

    public String formatRemaining() {
        long calculated = millions - calculateMillions();
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

    @Override
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();
        json.put("type", "temporary");
        json.put("recording", recording);
        json.put("millions", millions);
        json.put("uuid", getRecorde().uuid());
        json.put("unique_id", getRecorde().modmdoUniqueId());
        json.put("name", getName());
        return json;
    }
}
