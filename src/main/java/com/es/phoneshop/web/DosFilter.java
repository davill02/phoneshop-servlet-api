package com.es.phoneshop.web;

import com.es.phoneshop.security.DosSecurityService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DosFilter implements Filter {
    public static final int T00_MANY_REQUEST = 429;

    private DosSecurityService dosSecurityService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        dosSecurityService = DosSecurityService.getInstance();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (dosSecurityService.isAllowed(request.getRemoteAddr())) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(T00_MANY_REQUEST);
        }
    }

    @Override
    public void destroy() {

    }

    public void setDosSecurityService(DosSecurityService dosSecurityService) {
        this.dosSecurityService = dosSecurityService;
    }
}
