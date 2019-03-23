package cn.com.cyber.util;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "code")
public class CodeEnv {

    private String project_environment;

    private String socket_url;
    private Integer socket_server_port;
    private Integer socket_client_port;
    private Integer request_sleeptime;
    private Integer request_maxtime;
    private Integer threadPoolSize;
    private Integer  cache_time;
    private Integer  file_sever_port;
    private String  file_save_path;
    private Map<Integer,String> message;

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

    public Integer getSocket_server_port() {
        return socket_server_port;
    }

    public void setSocket_server_port(Integer socket_server_port) {
        this.socket_server_port = socket_server_port;
    }

    public Integer getSocket_client_port() {
        return socket_client_port;
    }

    public void setSocket_client_port(Integer socket_client_port) {
        this.socket_client_port = socket_client_port;
    }

    public Integer getRequest_sleeptime() {
        return request_sleeptime;
    }

    public void setRequest_sleeptime(Integer request_sleeptime) {
        this.request_sleeptime = request_sleeptime;
    }

    public Integer getRequest_maxtime() {
        return request_maxtime;
    }

    public void setRequest_maxtime(Integer request_maxtime) {
        this.request_maxtime = request_maxtime;
    }

    public String getMessage(Integer key) {
        return message.get(key)==null?"":message.get(key);
    }

    public void setMessage(Map<Integer, String> message) {
        this.message = message;
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

    public Integer getCache_time() {
        return cache_time;
    }

    public void setCache_time(Integer cache_time) {
        this.cache_time = cache_time;
    }

    public String getFile_save_path() {
        return file_save_path;
    }

    public void setFile_save_path(String file_save_path) {
        this.file_save_path = file_save_path;
    }
}
