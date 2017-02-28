package com.ecreditpal.maas.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author lifeng
 * @version 1.0 on 2017/2/27.
 */
@Slf4j
public class IPBasedRateLimiter {

    private static volatile IPBasedRateLimiter instance;
    private static volatile boolean initialized = false;
    private static final Object initLock = new Object();
    RateLimiter defaultRateLimiter = RateLimiter.create(160);
    ReentrantLock lock = new ReentrantLock();


    // guava cache is thread-safe
    private static volatile Cache<String, RateLimiter> cache;


    private IPBasedRateLimiter() {
        log.info("IPBasedRateLimiter初始化");
        cache = CacheBuilder.newBuilder().maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES).build();
    }

    public static IPBasedRateLimiter getInstance() {
        if (initialized) {
            return instance;
        }

        synchronized (initLock) {
            if (!initialized) {
                instance = new IPBasedRateLimiter();
                initialized = true;
            }
            return instance;
        }
    }

    public boolean tryAcquire(String ipAddress) {
        RateLimiter rateLimiter = cache.getIfPresent(ipAddress);
        if (rateLimiter == null) {
            synchronized (ipAddress.intern()) {
                if ((rateLimiter = cache.getIfPresent(ipAddress)) !=null) {
                   return rateLimiter.tryAcquire(200,TimeUnit.MILLISECONDS);
                }
                    rateLimiter = RateLimiter.create(500);
                    cache.put(ipAddress, rateLimiter);
                    log.info("create a new ratelimiter for ip: {}", ipAddress);
                return defaultRateLimiter.tryAcquire();
            }
        } else {
            return rateLimiter.tryAcquire();
        }
    }
}