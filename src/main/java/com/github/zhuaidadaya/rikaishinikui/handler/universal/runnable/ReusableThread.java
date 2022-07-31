package com.github.zhuaidadaya.rikaishinikui.handler.universal.runnable;

import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;

import java.util.concurrent.*;

public class ReusableThread {
    private final Thread executor;
    private Temporary action;
    private boolean running = true;
    private boolean wait = false;

    public ReusableThread(Temporary action) {
        this.action = action;
        executor = new Thread(this::start);
        executor.start();
    }

    public void start() {
        while (running) {
            if (wait) {
                EntrustExecution.tryTemporary(() -> {
                    TimeUnit.MILLISECONDS.sleep(1);
                }, ex -> finish());
            } else {
                EntrustExecution.tryTemporary(() -> this.action.apply());

                wait = true;
            }
        }
    }

    public void finish() {
        running = false;
        executor.interrupt();
    }

    public boolean isAlive() {
        return ! wait;
    }

    public boolean execute() {
        if (this.running) {
            if (wait) {
                wait = false;
                return true;
            }
        }
        return false;
    }

    public boolean execute(Temporary action) {
        if (this.running) {
            if (wait) {
                this.action = action;
                wait = false;
                return true;
            }
        }
        return false;
    }

    public void setName(String name) {
        executor.setName(name);
    }
}
