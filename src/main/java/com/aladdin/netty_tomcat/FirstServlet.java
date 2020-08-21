package com.aladdin.netty_tomcat;


import com.aladdin.netty_tomcat.core.Request;
import com.aladdin.netty_tomcat.core.Response;
import com.aladdin.netty_tomcat.core.Servlet;

import java.io.IOException;

/**
 * @author lgc
 */
public class FirstServlet extends Servlet {
    @Override
    public void doGet(Request request, Response response) throws IOException {
        this.doPost(request,response);
    }

    @Override
    public void doPost(Request request, Response response) throws IOException {
        response.write("this is first servlet");
    }
}
