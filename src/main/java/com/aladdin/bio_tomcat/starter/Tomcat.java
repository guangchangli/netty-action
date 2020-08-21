package com.aladdin.bio_tomcat.starter;

import com.aladdin.bio_tomcat.core.Request;
import com.aladdin.bio_tomcat.core.Response;
import com.aladdin.bio_tomcat.core.Servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author lgc
 */
public class Tomcat {
    private int port = 8080;
    private ServerSocket server;
    private Map<String, Servlet> servletMapping = new HashMap<>();
    private Properties webXml = new Properties();

    /**
     * 读配置文件初始化
     */
    private void init() {
        try {

            //加载 web.xml 同时初始化 ServletMapping 对象
            String WEB_INF = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(WEB_INF + "web.properties");
            webXml.load(fis);
            for (Object k : webXml.keySet()) {
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webXml.getProperty(key);
                    String className = webXml.getProperty(servletName + ".className");
                    //单实例 多线程
                    Servlet obj = (Servlet) Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动
     */
    public void start() {
        //加载配置文件 初始化 servletMapping
        init();
        try {
            server = new ServerSocket(this.port);
            System.out.println("Aladdin 牌 TOMCAT 已经启动，监听的端口是：" + this.port);
            //等待请求
            while (true) {
                Socket client = server.accept();
                process(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理请求
     */
    private void process(Socket client) throws IOException {

        InputStream is = client.getInputStream();
        OutputStream os = client.getOutputStream();

        Request request = new Request(is);
        Response response = new Response(os);

        String url = request.getUrl();
        if (servletMapping.containsKey(url)) {
            servletMapping.get(url).service(request, response);
        } else {
            response.write("404 - Not Found");
        }
        os.flush();
        os.close();
        is.close();
        client.close();
    }
}
