package com.es.phoneshop.web;


import com.es.phoneshop.model.product.ProductDao;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;
@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest{
    public static final String ATTR_PRODUCT = "product";
    public static final String PAGE_PATH = "/WEB-INF/pages/productDetails.jsp";
    public static final String STANDART_PATH = "/12";
    private ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();
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
    @Before
    public void setup() throws ServletException {
        servlet.init(mockConfig);
        servlet.setProductDao(productDao);
    }
    @Test
    public void shouldGetProduct() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn(STANDART_PATH);
        when(request.getRequestDispatcher(any())).thenReturn(dispatcher);

        servlet.doGet(request,response);

        verify(request).setAttribute(eq(ATTR_PRODUCT),any());
        verify(request).getRequestDispatcher(eq(PAGE_PATH));
    }
}