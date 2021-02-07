package com.es.phoneshop.listener;

import com.es.phoneshop.model.product.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;


@RunWith(MockitoJUnitRunner.class)
public class ProductDaoDemodataServletContextListenerTest {
    public static final String PARAM_START_WITH_DEFAULT_PRODUCTS = "startWithDefaultProducts";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final int DEFAULT_SAVE_OCCUR = 13;
    @Mock
    ServletContextEvent mockServletContextEvent;
    @Mock
    ProductDao mockProductDao;
    @Mock
    ServletContext mockContext;
    ProductDaoDemodataServletContextListener listener;


    @Before
    public void setup() {
        listener = new ProductDaoDemodataServletContextListener(mockProductDao);
    }

    @Test
    public void shouldStartWithDefaultProducts() {
        when(mockServletContextEvent.getServletContext()).thenReturn(mockContext);
        when(mockContext.getInitParameter(PARAM_START_WITH_DEFAULT_PRODUCTS)).thenReturn(TRUE);

        listener.contextInitialized(mockServletContextEvent);

        verify(mockProductDao, times(DEFAULT_SAVE_OCCUR)).save(any());
    }

    @Test
    public void shouldStartEmpty() {
        when(mockServletContextEvent.getServletContext()).thenReturn(mockContext);
        when(mockContext.getInitParameter(PARAM_START_WITH_DEFAULT_PRODUCTS)).thenReturn(FALSE);

        listener.contextInitialized(mockServletContextEvent);

        verify(mockProductDao, never()).save(any());
    }
}
