package com.ecreditpal.maas.common.utils.http;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

/**
 * @author lifeng
 * @CreateTime 2017/4/13.
 */

public class ConnectionKeepAliveStrategy implements org.apache.http.conn.ConnectionKeepAliveStrategy {
    private long timeout;

    public ConnectionKeepAliveStrategy(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
        while (it.hasNext()) {
            HeaderElement he = it.nextElement();
            String param = he.getName();
            String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return Long.parseLong(value) * 1000;
                }
                catch (NumberFormatException e) {

                }
            }
        }

        return timeout * 1000;
    }

}
