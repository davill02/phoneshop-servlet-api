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
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static com.es.phoneshop.web.ServletsConstants.ATTR_EXCEPTION_MAP;
import static com.es.phoneshop.web.ServletsConstants.CART_PAGE_PATH;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ERROR;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ERROR_VALUE_OUT_OF_STOCK;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ID;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUANTITY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
    public static final String STRING = "string";
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletConfig config;
    @Mock
    private CartService cartService;
    @Mock
    private Map<Long, List<String>> map;

    private final CartPageServlet servlet = new CartPageServlet();
    private String[] quantity;
    private String[] id;


    @Before
    public void setUp() throws Exception {
        servlet.init(config);
        servlet.setCartService(cartService);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
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
        setStringsArrays();
        when(request.getParameterValues(eq(PARAM_QUANTITY))).thenReturn(quantity);
        when(request.getParameterValues(eq(PARAM_ID))).thenReturn(id);

        servlet.doPost(request, response);

        verify(cartService, times(quantity.length)).update(any(), anyLong(), anyInt());
    }

    private void setStringsArrays() {
        quantity = new String[]{"1", "2", "2", "3"};
        id = new String[]{"3", "4", "4", "3"};
    }

    @Test
    public void shouldHandleParseException() throws IOException, ServletException, InvalidQuantityException, OutOfStockException {
        setStringsArrays();
        quantity[1] = STRING;
        when(request.getParameterValues(eq(PARAM_QUANTITY))).thenReturn(quantity);
        when(request.getParameterValues(eq(PARAM_ID))).thenReturn(id);

        servlet.doPost(request, response);

        verify(cartService, times(quantity.length - 1)).update(any(), anyLong(), anyInt());
    }

    @Test
    public void shouldHandleInvalidQuantityException() throws IOException, ServletException, InvalidQuantityException, OutOfStockException {
        setStringsArrays();
        setExceptionMapAndRequestStubbing(InvalidQuantityException.class);

        servlet.doPost(request, response);

        verify(cartService, times(quantity.length)).update(any(), anyLong(), anyInt());
        verify(map, times(quantity.length)).put(anyLong(), any());
    }

    private void setExceptionMapAndRequestStubbing(Class doThrowExceptionClass) throws InvalidQuantityException, OutOfStockException {
        servlet.setExceptionMap(map);
        servlet.setTested(true);
        doThrow(doThrowExceptionClass).when(cartService).update(any(), any(), anyInt());
        when(request.getParameterValues(eq(PARAM_QUANTITY))).thenReturn(quantity);
        when(request.getParameterValues(eq(PARAM_ID))).thenReturn(id);

    }

    @Test
    public void shouldHandleOutOfStockException() throws IOException, ServletException, InvalidQuantityException, OutOfStockException {
        setStringsArrays();
        setExceptionMapAndRequestStubbing(OutOfStockException.class);

        servlet.doPost(request, response);

        verify(cartService, times(quantity.length)).update(any(), anyLong(), anyInt());
        verify(map, times(quantity.length)).put(anyLong(), any());
    }

    @Test
    public void shouldHandleClassCastException() throws InvalidQuantityException, OutOfStockException, IOException, ServletException {
        setStringsArrays();
        setExceptionMapAndRequestStubbing(ClassCastException.class);

        servlet.doPost(request, response);

        verify(cartService, times(quantity.length)).update(any(), anyLong(), anyInt());
        verify(map, times(quantity.length)).put(anyLong(), any());
    }

    @Test
    public void shouldNormalizeCart() throws ServletException, IOException {
        when(request.getParameter(PARAM_ERROR)).thenReturn(PARAM_ERROR_VALUE_OUT_OF_STOCK);

        servlet.doGet(request, response);

        verify(cartService).normalizeCart(any());
    }
}


