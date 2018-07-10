package cn.com.cyber;

import cn.com.cyber.socket.SocketServer;
import cn.com.cyber.socket.SocketServerForward;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        //起socket服务
        SocketServer server = new SocketServer();
//        SocketServerForward server = new SocketServerForward();
        server.startSocketServer(8081);
    }
}