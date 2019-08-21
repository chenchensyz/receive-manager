package cn.com.cyber.runnable;

import java.util.concurrent.*;

public class MyThreadPool {

    //    private static ExecutorService executorPool;
    private static ThreadPoolExecutor executorPool;

    private static synchronized void ThreadPoolConfig() {
//        executorPool = Executors.newFixedThreadPool(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorPool = new ThreadPoolExecutor(100, 100, 1, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
        executorPool.allowCoreThreadTimeOut(true);
        CountErrThread countErr = new CountErrThread(executorPool, 1800);
        Thread countErrThread = new Thread(countErr);
        countErrThread.start();
    }

    public static ThreadPoolExecutor getThreadPool() {
        if (executorPool == null) {
            ThreadPoolConfig();
        }
        return executorPool;
    }

    public static void init() {
        if (executorPool == null) {
            ThreadPoolConfig();
        }
    }
}
