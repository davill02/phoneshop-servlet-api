package com.es.phoneshop.security;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DosSecurityServiceTest {
    private static String IP_1 = "192.02.20.23";
    private static String IP_2 = "192.04.20.23";

    private DosSecurityService service = DosSecurityService.getInstance();


    @Test
    public void shouldAllowed() {
        boolean result = service.isAllowed(IP_1);

        assertTrue(result);
    }

    @Test
    public void shouldNotAllowed() {
        makeMaxCountRequests(IP_1);

        boolean result = service.isAllowed(IP_1);

        assertFalse(result);
    }

    private void makeMaxCountRequests(String ip) {
        for (int i = 0; i < DosSecurityService.MAX_COUNT_PER_MINUTE; i++) {
            service.isAllowed(ip);
        }
    }

    @Test
    public void shouldNotAllowedIp1AndAllowedIp2(){
        makeMaxCountRequests(IP_1);

        boolean result = service.isAllowed(IP_2);

        assertTrue(result);
    }

}