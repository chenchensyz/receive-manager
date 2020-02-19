package cn.com.cyber;

import cn.com.cyber.runnable.MyThreadPool;
import cn.com.cyber.runnable.SendFileThread;
import cn.com.cyber.socket.SocketClient;
import cn.com.cyber.socket.SpringUtil;
import cn.com.cyber.util.CodeUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;


@SpringBootApplication
public class WebApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);

        Environment env = SpringUtil.getBean(Environment.class);
        if (Boolean.valueOf(env.getProperty(CodeUtil.SOCKET_OPEN))) {
            MyThreadPool.init(); //启动线程
            if (Integer.valueOf(env.getProperty(CodeUtil.SOCKET_PORT)) == 8081) {
                MyThreadPool.getThreadPool().execute(new SendFileThread());
            }
//        //启动Socket
            SocketClient.init();
        }
    }
}