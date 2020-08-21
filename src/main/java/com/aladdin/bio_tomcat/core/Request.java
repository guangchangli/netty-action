package com.aladdin.bio_tomcat.core;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lgc
 */
public class Request {
    @Getter
    private String method;
    @Getter
    private String url;

    public Request(InputStream in) {
        try {

            //获取 http 内容
            String content = "";
            byte[] buff = new byte[1024];
            int len = 0;
            if ((len = in.read(buff)) > 0) {
                content = new String(buff, 0, len);
            }
            String line = content.split("\\n")[0];
            String[] arr = line.split("\\s");
            this.method = arr[0];
            this.url = arr[1].split("\\?")[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
