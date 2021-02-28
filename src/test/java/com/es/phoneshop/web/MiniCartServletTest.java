package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
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
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.class)
public class MiniCartServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private HttpSession session;
    private final MiniCartServlet servlet = new MiniCartServlet();

    @Before
    public void setup() throws ServletException {
        servlet.init(config);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldInclude() throws ServletException, IOException {
        servlet.doGet(request,response);

        verify(request).setAttribute(eq(ATTR_CART),any(Cart.class));
        verify(requestDispatcher).include(request,response);
    }

    @Test
    public void shouldDoGetInDoPost() throws ServletException, IOException {
        servlet.doPost(request,response);

        verify(request).setAttribute(eq(ATTR_CART),any(Cart.class));
        verify(requestDispatcher).include(request,response);
    }
}