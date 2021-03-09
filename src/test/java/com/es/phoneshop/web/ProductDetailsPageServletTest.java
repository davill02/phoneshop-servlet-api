package com.es.phoneshop.web;

import static com.es.phoneshop.web.ServletsConstants.*;
import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.recentlyviewed.RecentlyViewed;
import com.es.phoneshop.recentlyviewed.RecentlyViewedService;
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
import java.util.Locale;

import static com.es.phoneshop.web.ServletsExceptionMessages.CANT_PARSE_VALUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    private static final String STANDARD_PATH = "/12";
    private static final String ANY_INT = "12";
    private static final String NOT_INT = "not int";

    @Mock
    private ServletContext servletContext;
    @Mock
    private ServletConfig mockConfig;
    @Mock
    private ProductDao productDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher dispatcher;

    @Mock
    private HttpSession session;
    @Mock
    private CartService service;
    @Mock
    private Product product;
    @Mock
    private RecentlyViewedService recentlyViewedService;

    private final ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();


    @Before
    public void setup() throws ServletException {
        servlet.init(mockConfig);
        servlet.setProductDao(productDao);
        servlet.setCartService(service);
        servlet.setRecentlyViewedService(recentlyViewedService);
        when(recentlyViewedService.getRecentlyViewed(any())).thenReturn(new RecentlyViewed());
        when(servlet.getServletContext()).thenReturn(servletContext);
        when(request.getParameter(PARAM_QUANTITY)).thenReturn(ANY_INT);
        when(request.getPathInfo()).thenReturn("/" + ANY_INT);
        when(productDao.getProduct(anyLong())).thenReturn(product);
        when(request.getSession()).thenReturn(session);
        when(request.getLocale()).thenReturn(Locale.getDefault());
    }

    @Test
    public void shouldGetProduct() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(STANDARD_PATH);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(ATTR_PRODUCT), any());
        verify(request).getRequestDispatcher(eq(DETAILS_PAGE_PATH));
    }

    @Test
    public void shouldPostProductWithoutExceptions() throws ServletException, IOException {
        servlet.doPost(request, response);

        verify(response).sendRedirect(any());
    }

    @Test
    public void shouldPostHandleOutOfStockException() throws OutOfStockException, ServletException, IOException, InvalidQuantityException {
        doThrow(new OutOfStockException()).when(service).add(any(), any(), anyInt());
        when(request.getPathInfo()).thenReturn(STANDARD_PATH);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request, times(1)).setAttribute(eq(ATTR_PRODUCT), any());
        verify(request).setAttribute(eq(ATTR_ERROR), any());
    }

    @Test
    public void shouldPostHandleNumberFormatException() throws ServletException, IOException {
        when(request.getParameter(PARAM_QUANTITY)).thenReturn(NOT_INT);
        when(request.getPathInfo()).thenReturn(STANDARD_PATH);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request, times(1)).setAttribute(eq(ATTR_PRODUCT), any());
        verify(request).setAttribute(eq(ATTR_ERROR), eq(CANT_PARSE_VALUE));
    }

}
