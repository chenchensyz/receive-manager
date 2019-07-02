package cn.com.cyber.runnable;

/**
 * 文件传输服务--线程池
 */

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUpPool {

        private static ExecutorService executorPool;

    private static synchronized void ThreadPoolConfig(int number) {
        executorPool = Executors.newFixedThreadPool(number);
    }

    public static ExecutorService getThreadPool(int number) {
        if (executorPool==null){
            ThreadPoolConfig(number);
        }
        return executorPool;
    }

    public static void init(){
        if (executorPool==null){
            ThreadPoolConfig(1);
        }
    }
}
