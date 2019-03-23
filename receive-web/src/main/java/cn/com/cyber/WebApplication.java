package cn.com.cyber;

import cn.com.cyber.runnable.MyThreadPool;
import cn.com.cyber.runnable.SendFileThread;
import cn.com.cyber.socket.SocketClient;
import cn.com.cyber.socket.SpringUtil;
import cn.com.cyber.util.CodeEnv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
        MyThreadPool.init(); //启动线程

        CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
        if (codeEnv.getSocket_port() == 8081) {
            MyThreadPool.getThreadPool().execute(new SendFileThread());
        }
//        //启动Socket
        SocketClient.init();
    }
}