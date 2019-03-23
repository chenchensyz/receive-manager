package cn.com.cyber;

import cn.com.cyber.client.SocketClient;
import cn.com.cyber.runnable.MyThreadPool;
import cn.com.cyber.sever.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;


@SpringBootApplication
@EnableCaching
public class ForwardApplication {

    public static void main(String[] args) {

        SpringApplication.run(ForwardApplication.class, args);
        //启动线程池
        MyThreadPool.init();

        //启动netty
        MyThreadPool.getThreadPool().execute(new NettyServer());

    }

    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //文件最大
        factory.setMaxFileSize("50MB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("80MB");
        return factory.createMultipartConfig();
    }
}