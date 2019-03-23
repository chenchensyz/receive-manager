package cn.com.cyber.runnable;

import cn.com.cyber.client.SocketClient;
import cn.com.cyber.util.CodeEnv;
import cn.com.cyber.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class);

    private String command;

    public WorkerThread(String s) {
        this.command = s;
    }

    @Override
    public void run() {
        try {
            //正式查询
            CodeEnv codeEnv = SpringUtil.getBean(CodeEnv.class);
            if ("inter".equals(codeEnv.getProject_environment())) {
                SocketClient.send(codeEnv.getSocket_url(), codeEnv.getSocket_client_port(), command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return this.command;
    }
}
