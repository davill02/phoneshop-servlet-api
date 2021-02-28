package com.es.phoneshop.web;

import com.es.phoneshop.order.Order;
import com.es.phoneshop.order.OrderDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.es.phoneshop.web.ServletsConstants.ATTR_ORDER;
import static com.es.phoneshop.web.ServletsConstants.OVERVIEW_PAGE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OverviewPageServletTest {

    public static final String NOT_NULL_STRING = "Some String";
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private OrderDao orderDao;

    private final OverviewPageServlet servlet = new OverviewPageServlet();

    @Before
    public void setUp() throws Exception {
        servlet.setOrderDao(orderDao);
        when(orderDao.getOrder(any())).thenReturn(new Order());
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);
        when(request.getPathInfo()).thenReturn(NOT_NULL_STRING);
    }

    @Test
    public void shouldAddOrder() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq(ATTR_ORDER), any(Order.class));
        verify(request).getRequestDispatcher(eq(OVERVIEW_PAGE_PATH));
        verify(dispatcher).forward(eq(request), eq(response));
    }
}