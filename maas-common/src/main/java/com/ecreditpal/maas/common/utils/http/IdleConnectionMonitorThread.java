package com.ecreditpal.maas.common.utils.http;

import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author lifeng
 * @CreateTime 2017/4/13.
 */
public class IdleConnectionMonitorThread extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(IdleConnectionMonitorThread.class);

    private final PoolingClientConnectionManager connMgr;

    private long expireTime;

    public IdleConnectionMonitorThread(PoolingClientConnectionManager connMgr, long expireTime) {
        super();
        this.connMgr = connMgr;
        this.expireTime = expireTime;
    }

    @Override
    public void run() {
        while (true) {
            try {
                logger.debug("connection stats: {}", connMgr.getTotalStats());
                Thread.sleep(expireTime);
                connMgr.closeExpiredConnections();
                connMgr.closeIdleConnections(expireTime, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                logger.error("cannot close expired/idle connection: ", e);
                continue;
            }
        }
    }
}
