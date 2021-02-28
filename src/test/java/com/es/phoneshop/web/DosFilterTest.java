package com.es.phoneshop.web;

import com.es.phoneshop.security.DosSecurityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DosFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private DosSecurityService service;

    private DosFilter filter = new DosFilter();

    @Before
    public void setUp(){
        filter.setDosSecurityService(service);
    }
    @Test
    public void allowedRequest() throws IOException, ServletException {
        when(service.isAllowed(any())).thenReturn(true);

        filter.doFilter(request,response,chain);

        verify(chain).doFilter(eq(request),eq(response));
    }

    @Test
    public void notAllowedRequest() throws IOException, ServletException {
        when(service.isAllowed(any())).thenReturn(false);

        filter.doFilter(request,response,chain);

        verify(response).setStatus(eq(DosFilter.T00_MANY_REQUEST));
    }

}