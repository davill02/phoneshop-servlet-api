package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.es.phoneshop.web.ServletsConstants.PRODUCTS_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteCartItemServletTest {
    public static final String ID = "232";
    public static final String NOT_ID = "sfs";
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;
    @Mock
    private CartService cartService;
    @Mock
    private ServletContext context;

    private DeleteCartItemServlet servlet = new DeleteCartItemServlet();

    @Before
    public void setup() throws ServletException {
        when(config.getServletContext()).thenReturn(context);
        when(context.getContextPath()).thenReturn(PRODUCTS_PATH);
        servlet.init(config);
        servlet.setCartService(cartService);
        when(cartService.getCart(any())).thenReturn(new Cart());
    }

    @Test
    public void shouldIgnoreDelete() throws IOException {
        when(request.getPathInfo()).thenReturn(NOT_ID);

        servlet.doPost(request, response);

        verify(cartService, never()).delete(any(), any());
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void shouldDeleteItem() throws IOException {
        when(request.getPathInfo()).thenReturn(ID);

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
        verify(cartService).delete(any(Cart.class), anyLong());
    }
}