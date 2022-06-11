package com.github.cao.awa.hyacinth.logging;

import com.github.cao.awa.modmdo.storage.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.*;
import com.github.zhuaidadaya.rikaishinikui.handler.universal.entrust.function.*;

import java.io.*;

public class GlobalTracker {
    private BufferedWriter writer;

    public GlobalTracker() {
        EntrustExecution.tryTemporary(() -> {
            File file = new File("logs/tracker/latest.log");
            file.getParentFile().mkdirs();
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
        });
    }

    public void submit(String message) {
        if (SharedVariables.testing) {
            String data = PrintUtil.tacker(Thread.currentThread().getStackTrace(), - 1, 2, message).shortPrint();
            EntrustExecution.tryTemporary(() -> {
                if (writer != null) {
                    writer.write(data);
                    writer.flush();
                }
            });
        } else {
            SharedVariables.LOGGER.info(message);
        }
    }

    public void submit(String message, Temporary... actons) {
        for (Temporary temporary : actons) {
            temporary.apply();
            submit(message);
        }
    }

    public void submit(String message, Throwable throwable) {
        if (SharedVariables.testing) {
            String data = PrintUtil.tacker(throwable.getStackTrace(), - 1, 2, message).shortPrint();
            EntrustExecution.tryTemporary(() -> {
                if (writer != null) {
                    writer.write(data);
                    writer.flush();
                }
            });
        } else {
            SharedVariables.LOGGER.info(message, throwable);
        }
    }

    public void submit(Thread prent, String message) {
        if (SharedVariables.testing) {
            String data = PrintUtil.tacker(Thread.currentThread().getStackTrace(), - 1, 2, message).shortPrint();
            EntrustExecution.tryTemporary(() -> {
                if (writer != null) {
                    writer.write(data);
                    writer.flush();
                }
            });
        } else {
            SharedVariables.LOGGER.info(message);
        }
    }

    public void submit(Thread prent, String message, Temporary... actons) {
        for (Temporary temporary : actons) {
            temporary.apply();
            submit(prent, message);
        }
    }

    public void submit(Thread prent, String message, Throwable throwable) {
        if (SharedVariables.testing) {
            String data = PrintUtil.tacker(prent, throwable.getStackTrace(), - 1, 2, message).shortPrint();
            EntrustExecution.tryTemporary(() -> {
                if (writer != null) {
                    writer.write(data);
                    writer.flush();
                }
            });
        } else {
            SharedVariables.LOGGER.info(message, throwable);
        }
    }
}
