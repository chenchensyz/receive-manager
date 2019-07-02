package cn.com.cyber.runnable;

/**
 * 消息处理--任务处理
 */
import java.util.concurrent.*;

public class MyThreadPool {

    //    private static ExecutorService executorPool;
    private static ThreadPoolExecutor executorPool;

    private static synchronized void ThreadPoolConfig() {
//        executorPool = Executors.newFixedThreadPool(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorPool = new ThreadPoolExecutor(1000, 1000, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);
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
