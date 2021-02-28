package com.es.phoneshop.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.es.phoneshop.web.ServletsConstants.LIST_PAGE_PATH;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ORDER;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUERY;
import static com.es.phoneshop.web.ServletsConstants.PARAM_SORT;
import static com.es.phoneshop.web.ServletsConstants.PRODUCTS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    private final ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
    }

    @Test
    public void shouldDoGetWithEmptyParameter() throws ServletException, IOException {
        when(request.getParameter(eq(PARAM_ORDER))).thenReturn("");
        when(request.getParameter(PARAM_QUERY)).thenReturn("");
        when(request.getParameter(PARAM_SORT)).thenReturn("");

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(PRODUCTS), any());
        verify(request).getRequestDispatcher(eq(LIST_PAGE_PATH));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq(PRODUCTS), any());
        verify(request).getRequestDispatcher(eq(LIST_PAGE_PATH));
        verify(requestDispatcher).forward(request, response);
    }

}
