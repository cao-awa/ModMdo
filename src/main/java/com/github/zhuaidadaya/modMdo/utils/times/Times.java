package com.github.zhuaidadaya.modmdo.utils.times;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Times {
    public static String getTime(TimeType timeType) {
        if(timeType == TimeType.ALL) {
            return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
        } else if(timeType == TimeType.AS_SECOND) {
            return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        } else if(timeType == TimeType.AS_MINUTE) {
            return new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());
        } else if(timeType == TimeType.AS_CLOCK) {
            return new SimpleDateFormat("yyyy-MM-dd_HH").format(new Date());
        } else if(timeType == TimeType.AS_DAY) {
            return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        } else if(timeType == TimeType.AS_MONTH) {
            return new SimpleDateFormat("yyyy-MM").format(new Date());
        } else if(timeType == TimeType.AS_YEAR) {
            return new SimpleDateFormat("yyyy").format(new Date());
        } else if(timeType == TimeType.LOG) {
            return new SimpleDateFormat("[HH:mm:ss] ").format(new Date());
        } else if(timeType == TimeType.LONG_LOG) {
            return new SimpleDateFormat("[yyyy-MM-dd+HH:mm:ss:SSS] ").format(new Date());
        } else {
            return "";
        }
    }
}
