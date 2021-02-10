package com.es.phoneshop.web;


import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
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
    public static final String ATTR_PRODUCT = "product";
    public static final String PAGE_PATH = "/WEB-INF/pages/productDetails.jsp";
    public static final String STANDARD_PATH = "/12";
    public static final String ANY_INT = "12";
    public static final String NOT_INT = "not int";

    private final ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();
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

    @Before
    public void setup() throws ServletException {
        servlet.init(mockConfig);
        servlet.setProductDao(productDao);
        servlet.setCartService(service);
        when(servlet.getServletContext()).thenReturn(servletContext);
        when(request.getParameter(ProductDetailsPageServlet.PARAM_QUANTITY)).thenReturn(ANY_INT);
        when(request.getPathInfo()).thenReturn("/" + ANY_INT);
        when(productDao.getProduct(anyLong())).thenReturn(product);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldGetProduct() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(STANDARD_PATH);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq(ATTR_PRODUCT), any());
        verify(request).getRequestDispatcher(eq(PAGE_PATH));
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

        verify(request, times(2)).setAttribute(eq(ProductDetailsPageServlet.ATTR_PRODUCT), any());
        verify(request).setAttribute(eq(ProductDetailsPageServlet.ATTR_ERROR), any());
    }

    @Test
    public void shouldPostHandleNumberFormatException() throws OutOfStockException, ServletException, IOException {
        when(request.getParameter(ProductDetailsPageServlet.PARAM_QUANTITY)).thenReturn(NOT_INT);
        when(request.getPathInfo()).thenReturn(STANDARD_PATH);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request, times(2)).setAttribute(eq(ProductDetailsPageServlet.ATTR_PRODUCT), any());
        verify(request).setAttribute(eq(ProductDetailsPageServlet.ATTR_ERROR), eq(ProductDetailsPageServlet.NOT_A_NUMBER));
    }

}
