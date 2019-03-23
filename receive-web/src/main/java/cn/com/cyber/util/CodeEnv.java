package cn.com.cyber.util;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "code")
public class CodeEnv {

    private String project_environment;

    private String socket_url;
    private Integer socket_port;
    private Integer threadPoolSize;
    private Integer  file_sever_port;
    private String  file_save_path;

    public String getProject_environment() {
        return project_environment;
    }

    public void setProject_environment(String project_environment) {
        this.project_environment = project_environment;
    }

    public String getSocket_url() {
        return socket_url;
    }

    public void setSocket_url(String socket_url) {
        this.socket_url = socket_url;
    }

    public Integer getSocket_port() {
        return socket_port;
    }

    public void setSocket_port(Integer socket_port) {
        this.socket_port = socket_port;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(Integer threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public Integer getFile_sever_port() {
        return file_sever_port;
    }

    public void setFile_sever_port(Integer file_sever_port) {
        this.file_sever_port = file_sever_port;
    }

    public String getFile_save_path() {
        return file_save_path;
    }

    public void setFile_save_path(String file_save_path) {
        this.file_save_path = file_save_path;
    }
}
