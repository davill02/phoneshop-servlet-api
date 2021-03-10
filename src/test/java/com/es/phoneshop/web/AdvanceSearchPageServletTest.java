package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.search.emuns.SearchType;
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
import java.io.IOException;
import java.util.Map;

import static com.es.phoneshop.web.ServletsConstants.PARAM_MAX_PRICE;
import static com.es.phoneshop.web.ServletsConstants.PARAM_MIN_PRICE;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUERY;
import static com.es.phoneshop.web.ServletsConstants.PARAM_SEARCH_TYPE;
import static com.es.phoneshop.web.ServletsConstants.PRODUCTS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdvanceSearchPageServletTest {
    public static final String STRING = "String";
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
    private ProductDao productDao;
    AdvanceSearchPageServlet servlet = new AdvanceSearchPageServlet();

    @Before
    public void setup() throws ServletException {
        servlet.setProductDao(productDao);
        when(request.getRequestDispatcher(any())).thenReturn(requestDispatcher);
    }

    @Test
    public void shouldDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq(PRODUCTS), any());
        verify(requestDispatcher).forward(eq(request), eq(response));
    }

    @Test
    public void shouldDoGetWithAnyValidPriceAndSearchType() throws ServletException, IOException {
        when(request.getParameter(eq(PARAM_QUERY))).thenReturn("");
        when(request.getParameter(eq(PARAM_MAX_PRICE))).thenReturn("400");
        when(request.getParameter(eq(PARAM_MIN_PRICE))).thenReturn("200");
        when(request.getParameter(eq(PARAM_SEARCH_TYPE))).thenReturn(SearchType.ANY_WORD.toString());

        servlet.doGet(request, response);
        Map<String, String> result = servlet.getExceptions();

        verify(request).setAttribute(eq(PRODUCTS), any());
        verify(requestDispatcher).forward(eq(request), eq(response));
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldDoGetWithAnyInvalidPriceAndSearchType() throws ServletException, IOException {
        when(request.getParameter(eq(PARAM_QUERY))).thenReturn("");
        when(request.getParameter(eq(PARAM_MAX_PRICE))).thenReturn("200");
        when(request.getParameter(eq(PARAM_MIN_PRICE))).thenReturn("400");
        when(request.getParameter(eq(PARAM_SEARCH_TYPE))).thenReturn(SearchType.ANY_WORD.toString());

        servlet.doGet(request, response);
        Map<String, String> result = servlet.getExceptions();

        verify(request).setAttribute(eq(PRODUCTS), any());
        verify(requestDispatcher).forward(eq(request), eq(response));
        assertFalse(result.isEmpty());
        assertNotNull(result.get("max min"));
    }
    @Test
    public void shouldDoGetWithInvalidPriceByNumberAndSearchType() throws ServletException, IOException {
        when(request.getParameter(eq(PARAM_QUERY))).thenReturn("");
        when(request.getParameter(eq(PARAM_MAX_PRICE))).thenReturn(STRING);
        when(request.getParameter(eq(PARAM_MIN_PRICE))).thenReturn("400");
        when(request.getParameter(eq(PARAM_SEARCH_TYPE))).thenReturn(SearchType.ANY_WORD.toString());

        servlet.doGet(request, response);
        Map<String, String> result = servlet.getExceptions();

        verify(request).setAttribute(eq(PRODUCTS), any());
        verify(requestDispatcher).forward(eq(request), eq(response));
        assertFalse(result.isEmpty());
        assertNotNull(result.get(STRING));
    }

}