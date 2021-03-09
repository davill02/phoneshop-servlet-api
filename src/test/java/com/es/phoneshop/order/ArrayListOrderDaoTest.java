package com.es.phoneshop.order;

import com.es.phoneshop.order.exceptions.OrderNotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ArrayListOrderDaoTest {
    private static final int EMPTY = 0;
    public static final int ONE_ITEM = 1;
    public static final String NON_EXIST_ID = "werfgd";
    private OrderDao orderDao;

    @Before
    public void setup() {
        orderDao = ArrayListOrderDao.getInstance();
    }

    @After
    public void tearDown() {
        ((ArrayListOrderDao) orderDao).deleteAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgException() {
        orderDao.save(null);
    }

    @Test
    public void shouldDontSaveOrder() {
        Order order = new Order();

        orderDao.save(order);
        int resultSize = ((ArrayListOrderDao) orderDao).getSize();

        assertEquals(EMPTY, resultSize);
    }

    @Test
    public void shouldSaveOrder() {
        Order order = new Order();
        order.setSecureId(UUID.randomUUID().toString());

        orderDao.save(order);
        int resultSize = ((ArrayListOrderDao) orderDao).getSize();

        assertEquals(ONE_ITEM, resultSize);
    }

    @Test(expected = OrderNotFoundException.class)
    public void shouldThrowOrderNotFound() {
        orderDao.getOrder(NON_EXIST_ID);
    }

    @Test(expected = OrderNotFoundException.class)
    public void shouldThrowOrderNotFoundWithNull() {
        orderDao.getOrder(null);
    }

    @Test
    public void shouldFindOrder() {
        Order order = new Order();
        String id = UUID.randomUUID().toString();
        order.setSecureId(id);
        orderDao.save(order);

        Order result = orderDao.getOrder(id);

        assertEquals(order,result);
    }
}