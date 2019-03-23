package cn.com.cyber.runnable;

import java.util.concurrent.*;

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
