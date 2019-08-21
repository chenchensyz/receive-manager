package cn.com.cyber.runnable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

public class CountErrThread implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CountErrThread.class);

    private ThreadPoolExecutor executor;
    private int seconds;
    private boolean run = true;

    public CountErrThread(ThreadPoolExecutor executor, int delay) {
        this.executor = executor;
        this.seconds = delay;
    }

    public void shutdown() {
        this.run = false;
    }

    @Override
    public void run() {
        while (run) {
            LOG.info(String.format( "[monitor] [%d / %d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                    this.executor.getPoolSize(), this.executor.getCorePoolSize(), this.executor.getActiveCount(),
                    this.executor.getCompletedTaskCount(), this.executor.getTaskCount(), this.executor.isShutdown(),
                    this.executor.isTerminated()));
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
