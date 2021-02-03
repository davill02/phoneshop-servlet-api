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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDaoDemodataServletContextListenerTest {
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
        when(mockContext.getInitParameter("startWithDefaultProducts")).thenReturn("true");

        listener.contextInitialized(mockServletContextEvent);

        verify(mockProductDao, times(13)).save(any());
    }

    @Test
    public void shouldStartEmpty() {
        when(mockServletContextEvent.getServletContext()).thenReturn(mockContext);
        when(mockContext.getInitParameter("startWithDefaultProducts")).thenReturn("s;alkjfd");

        listener.contextInitialized(mockServletContextEvent);

        verify(mockProductDao, never()).save(any());
    }
}
