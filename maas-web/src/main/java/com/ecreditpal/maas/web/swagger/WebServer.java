package com.ecreditpal.maas.web.swagger;

import com.ecreditpal.maas.common.IPBasedRateLimiter;
import com.ecreditpal.maas.common.utils.file.ConfigurationManager;
import org.apache.commons.configuration.Configuration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author lifeng
 * @version 1.0 on 2017/2/16.
 */
public class WebServer {
    public static void main(String[] args) {
        Configuration configuration = ConfigurationManager.getConfiguration();
        Server server = new Server(configuration.getInt("http.port", 9080));

        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor(configuration.getString("maas.web","./maas-web/src/main/webapp/WEB-INF/web.xml"));
        String webapp = configuration.getString("maas.webapp","./maas-web/src/main/webapp");
        context.setResourceBase(webapp);
        context.setParentLoaderPriority(true);


        server.setHandler(context);
//        IPBasedRateLimiter.getInstance();

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}