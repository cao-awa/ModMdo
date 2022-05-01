package com.github.zhuaidadaya.modmdo.whitelist;

import com.github.zhuaidadaya.modmdo.utils.times.*;

public record TemporaryWhitelist(String name, long recording, long millions) {
    public boolean isValid() {
        return TimeUtil.processMillion(recording) < millions;
    }
}
