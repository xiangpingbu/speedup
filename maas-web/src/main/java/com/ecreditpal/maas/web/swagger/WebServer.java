package com.ecreditpal.maas.web.swagger;

import com.ecreditpal.maas.common.IPBasedRateLimiter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author lifeng
 * @version 1.0 on 2017/2/16.
 */
public class WebServer {
    public static void main(String[] args) {
        Server server = new Server(8888);

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor("./maas-web/src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase("./maas-web/src/main/webapp");
        context.setParentLoaderPriority(true);


        server.setHandler(context);
        IPBasedRateLimiter.getInstance();

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}