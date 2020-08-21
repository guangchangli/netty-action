package com.aladdin.netty_tomcat.starter;

import com.aladdin.netty_tomcat.core.Request;
import com.aladdin.netty_tomcat.core.Response;
import com.aladdin.netty_tomcat.core.Servlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * netty 支持多种协议
 *
 * @author lgc
 */
public class Tomcat {
    private int port = 8080;
    private Map<String, Servlet> servletMapping = new HashMap<>();
    private Properties webXml = new Properties();

    /**
     * 读配置文件初始化
     */
    private void init() {
        try {

            //加载 web.xml 同时初始化 ServletMapping 对象
            String webInf = this.getClass().getResource("/").getPath();
            FileInputStream fis = new FileInputStream(webInf + "web.properties");
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
        //netty 封装了NIO 的Reactor 模型 BOSS Worker
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            //配置参数
            server.group(bossGroup, workerGroup)
                    //主线程处理类
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        //客户端初始化开始
                        protected void initChannel(SocketChannel client) {
                            //无锁化串行编程
                            //netty 对 HTTP 的封装 对顺序有要求
                            client.pipeline().addLast(new HttpResponseEncoder());
                            client.pipeline().addLast(new HttpRequestDecoder());
                            //业务逻辑处理
                            client.pipeline().addLast(new TomcatHandler());
                        }
                    })
                    //针对主线程配置 分配线程最大值 128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //针对子线程的连接，保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //启动服务器
            ChannelFuture f = server.bind(port).sync();
            System.out.println("TOMCAT_NETTY 已经启动，监听的端口是：" + port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public class TomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;
                Request request = new Request(ctx, req);
                Response response = new Response(ctx, req);
                //实际业务处理
                String url = request.getUrl();
                if (servletMapping.containsKey(url)) {
                    servletMapping.get(url).service(request, response);
                } else {
                    response.write("404 - Found");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

        }
    }

    public static void main(String[] args) {
        new Tomcat().start();
    }
}
