package cn.com.cyber;

import cn.com.cyber.fileUpload.UploadServer;
import cn.com.cyber.netty.NettyServer;
import cn.com.cyber.runnable.MyThreadPool;
import cn.com.cyber.runnable.SendFileThread;
import cn.com.cyber.socket.SocketClient;
import cn.com.cyber.util.CodeUtil;
import cn.com.cyber.util.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.servlet.MultipartConfigElement;


@SpringBootApplication
public class NettyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);

        Environment env = SpringUtil.getBean(Environment.class);
        if (Boolean.valueOf(env.getProperty(CodeUtil.SOCKET_OPEN))) {  //开启tcp服务
            //启动线程池
            MyThreadPool.init();
            //启动netty
            MyThreadPool.getThreadPool().execute(new NettyServer());
            if (Integer.valueOf(env.getProperty(CodeUtil.SOCKET_SERVER_PORT)) == 8081) {
                //启动文件传输
                MyThreadPool.getThreadPool().execute(new UploadServer());
                //发送文件
                if ("mobile".equals(env.getProperty(CodeUtil.PROJECT_ENVIRONMENT))) {
                    MyThreadPool.getThreadPool().execute(new SendFileThread());
                }

            }
            //启动Socket
            SocketClient.init();
        }
    }

    /**
     * 文件上传配置
     *
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