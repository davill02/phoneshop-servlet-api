package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
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
import java.util.Locale;
import java.util.Map;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static com.es.phoneshop.web.ServletsConstants.ATTR_EXCEPTION_MAP;
import static com.es.phoneshop.web.ServletsConstants.CART_PAGE_PATH;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ID;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUANTITY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
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
    @Mock
    private CartService cartService;

    private CartPageServlet servlet = new CartPageServlet();

    @Before
    public void setUp() throws Exception {
        servlet.init(config);
        servlet.setCartService(cartService);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(any())).thenReturn(new Cart());
        when(request.getLocale()).thenReturn(Locale.getDefault());
    }

    @Test
    public void shouldDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq(ATTR_CART), any(Cart.class));
        verify(request).setAttribute(eq(ATTR_EXCEPTION_MAP), any(Map.class));
        verify(request).getRequestDispatcher(CART_PAGE_PATH);
    }

    @Test
    public void shouldUpdateCart() throws IOException, ServletException, InvalidQuantityException, OutOfStockException {
        String[] quantity = {"1", "2", "2", "3"};
        String[] id = {"3", "4", "4", "3"};
        when(request.getParameterValues(eq(PARAM_QUANTITY))).thenReturn(quantity);
        when(request.getParameterValues(eq(PARAM_ID))).thenReturn(id);

        servlet.doPost(request, response);

        verify(cartService, times(quantity.length)).update(any(), anyLong(), anyInt());
    }


}