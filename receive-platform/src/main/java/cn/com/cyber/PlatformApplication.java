package cn.com.cyber;

import cn.com.cyber.runnable.ConnectThread;
import cn.com.cyber.runnable.MyThreadPool;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@MapperScan("cn.com.cyber.dao")
@SpringBootApplication
public class PlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlatformApplication.class, args);

        Environment env = SpringUtil.getBean(Environment.class);
        if (Boolean.valueOf(env.getProperty(CodeUtil.SOCKET_OPEN))) {
            MyThreadPool.init(); //启动线程
            MyThreadPool.getThreadPool().execute(new ConnectThread());
        }
    }
}