package com.github.cao.awa.modmdo.event.trigger.trace;

import java.io.*;

public record TriggerTrace(File file, int position, String name) {
    public String buildAt() {
        return " <at: " + file.getPath() + ", trigger position: " + position + "(" + name + ")>";
    }
}
