package com.es.phoneshop.security;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DosSecurityService {
    public static final int MAX_COUNT_PER_MINUTE = 20;
    public static final long SECOND = 1000L;
    public static final long COUNT_SECONDS = 60L;
    public static final int DELAY = 0;

    private static DosSecurityService service;

    private Map<String, Long> requestPerMinute = new ConcurrentHashMap();

    private DosSecurityService() {
        Timer timer = new Timer();
        TimerTask cycleTask = new TimerTask() {
            @Override
            public void run() {
                requestPerMinute.clear();
            }
        };
        timer.scheduleAtFixedRate(cycleTask, DELAY, SECOND * COUNT_SECONDS);
    }

    public static synchronized DosSecurityService getInstance() {
        if (service == null) {
            service = new DosSecurityService();
        }
        return service;
    }

    public boolean isAllowed(String ip) {
        Long count = requestPerMinute.get(ip);
        count = getCount(count);
        requestPerMinute.put(ip, count);
        return isAllowedCount(count);
    }

    private boolean isAllowedCount(Long count) {
        return count <= MAX_COUNT_PER_MINUTE;
    }

    private Long getCount(Long count) {
        if (count == null) {
            count = 1L;
        } else {
            count++;
        }
        return count;
    }
}
