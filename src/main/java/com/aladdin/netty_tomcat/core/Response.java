package com.aladdin.netty_tomcat.core;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.UnsupportedEncodingException;

/**
 * @author lgc
 */
public class Response {
    private ChannelHandlerContext ctx;
    private HttpRequest req;

    public Response(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String out) {
        try {
            if (out == null || out.length() == 0) {
                return;
            }
            //设置 http 以及请求头信息
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(out.getBytes("UTF-8"))
            );
            response.headers().set("Content-type", "text/html");
            ctx.write(response);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            ctx.flush();
            ctx.close();
        }
    }
}
