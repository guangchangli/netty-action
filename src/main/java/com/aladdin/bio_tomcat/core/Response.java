package com.aladdin.bio_tomcat.core;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lgc
 */
public class Response {
    private OutputStream out;

    public Response(OutputStream out) {
        this.out = out;
    }

    public void write(String s) throws IOException {
        //输出遵循 HTTP
        //状态吗为200
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\n")
                .append("Content-Type:text/html;\n")
                .append("\r\n")
                .append(s);
        out.write(sb.toString().getBytes());
    }
}
