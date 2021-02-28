package com.es.phoneshop.web;

import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.order.Order;
import com.es.phoneshop.order.OrderService;
import com.es.phoneshop.order.PaymentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import static com.es.phoneshop.web.ServletsConstants.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
    private static final String VALID_ADDRESS = "Valid_AddressValid";
    private static final String VALID_NAME = "Valid Name";
    private static final String VALID_PHONE_NUMBER = "+347347733";
    private static final String VALID_DATA = "1/20/2050";
    private static final String ANY_PATH = "";
    private static final String INVALID_ADDRESS = "invalid";
    public static final String NOT_DATE = "NOT_DATE";
    public static final String NOT_PAYMENT_TYPE = NOT_DATE;
    @Mock
    private OrderService orderService;
    @Mock
    private CartService cartService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private ServletContext context;
    @Mock
    private HttpSession session;

    private CheckoutPageServlet servlet = new CheckoutPageServlet();

    @Before
    public void setUp() throws ServletException {
        servlet.init(config);
        servlet.setCartService(cartService);
        servlet.setOrderService(orderService);
        servlet.setExceptionMap(new HashMap<>());
        when(request.getRequestDispatcher(CHECKOUT_PAGE_PATH)).thenReturn(requestDispatcher);
        when(config.getServletContext()).thenReturn(context);
        when(context.getContextPath()).thenReturn(ANY_PATH);
    }

    @Test
    public void shouldDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq(ATTR_EXCEPTION_MAP), any());
        verify(request).setAttribute(eq(ATTR_ORDER), any());
        verify(request).setAttribute(eq(ATTR_PAYMENT_TYPES), any());
        verify(requestDispatcher).forward(eq(request), eq(response));
    }

    @Test
    public void shouldDoPost() throws ServletException, IOException {
        createValidData();
        when(orderService.getOrder(any())).thenReturn(new Order());

        servlet.doPost(request, response);

        verify(response).sendRedirect(any());
        verify(orderService).placeOrder(any());
    }

    private void createValidData() {
        when(request.getParameter(eq(PARAM_ADDRESS))).thenReturn(VALID_ADDRESS);
        when(request.getParameter(eq(PARAM_PHONE))).thenReturn(VALID_PHONE_NUMBER);
        when(request.getParameter(eq(PARAM_LASTNAME))).thenReturn(VALID_NAME);
        when(request.getParameter(eq(PARAM_FIRSTNAME))).thenReturn(VALID_NAME);
        when(request.getParameter(eq(PARAM_DATE))).thenReturn(VALID_DATA);
        when(request.getParameter(eq(PARAM_PAYMENT_TYPE))).thenReturn(PaymentType.card.toString());
        when(request.getLocale()).thenReturn(Locale.US);
    }

    @Test
    public void shouldNotPlaceOrder() throws ServletException, IOException {
        createValidData();
        when(request.getParameter(eq(PARAM_ADDRESS))).thenReturn(INVALID_ADDRESS);

        servlet.doPost(request, response);

        verify(response, never()).sendRedirect(any());
        verify(orderService, never()).placeOrder(any());
    }

    @Test
    public void shouldNotPlaceOrderByInvalidDate() throws ServletException, IOException {
        createValidData();
        when(request.getParameter(eq(PARAM_DATE))).thenReturn(NOT_DATE);

        servlet.doPost(request, response);

        verify(response, never()).sendRedirect(any());
        verify(orderService, never()).placeOrder(any());
    }

    @Test
    public void shouldPlaceOrderWithDefaultType() throws ServletException, IOException {
        createValidData();
        when(request.getParameter(eq(PARAM_PAYMENT_TYPE))).thenReturn(NOT_PAYMENT_TYPE);
        Order result = new Order();
        when(orderService.getOrder(any())).thenReturn(result);

        servlet.doPost(request, response);

        verify(response).sendRedirect(any());
        verify(orderService).placeOrder(any());
        assertEquals(PaymentType.cash, result.getType());
    }

    @Test
    public void shouldNotPlaceOrderByNullParameter() throws IOException, ServletException {
        createValidData();
        when(request.getParameter(eq(PARAM_PHONE))).thenReturn(null);

        servlet.doPost(request, response);

        verify(response, never()).sendRedirect(any());
        verify(orderService, never()).placeOrder(any());
    }
}