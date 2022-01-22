package com.github.zhuaidadaya.modMdo.system;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class SystemUtil {
    public static String getCpuInfo(String getObj){
        try {
            SystemInfo systemInfo = new SystemInfo();
            CentralProcessor processor = systemInfo.getHardware().getProcessor();
            long[] prevTicks = processor.getSystemCpuLoadTicks();
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {

            }
            long[] ticks = processor.getSystemCpuLoadTicks();
            long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
            long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
            long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
            long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
            long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
            long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
            long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
            long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
            long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

            switch(getObj) {
                case "totalUsed" -> {
                    return new DecimalFormat("#.##%").format(1.0 - (idle * 1.0 / totalCpu));
                }
                case "cores" -> {
                    return String.valueOf(processor.getLogicalProcessorCount());
                }
                case "systemUsed" -> {
                    return new DecimalFormat("#.##%").format(cSys * 1.0 / totalCpu);
                }
                case "userUsed" -> {
                    return new DecimalFormat("#.##%").format(user * 1.0 / totalCpu);
                }
                case "wait" -> {
                    return new DecimalFormat("#.##%").format(iowait * 1.0 / totalCpu);
                }
            }

            return "Nan";
        } catch (Exception e) {
            return "Nan";
        }
    }

    public static String getCpuTotalUsed() {
        return getCpuInfo("totalUsed");
    }

    public static String getCpuWait() {
        return getCpuInfo("wait");
    }
}
