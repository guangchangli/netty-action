package com.aladdin.bio_tomcat;

import com.aladdin.bio_tomcat.core.Request;
import com.aladdin.bio_tomcat.core.Response;
import com.aladdin.bio_tomcat.core.Servlet;

import java.io.IOException;

/**
 * @author lgc
 */
public class SecondServlet extends Servlet {
    @Override
    public void doGet(Request request, Response response) throws IOException {
        this.doPost(request,response);
    }

    @Override
    public void doPost(Request request, Response response) throws IOException {
        response.write("this is second servlet");
    }
}
