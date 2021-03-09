package com.es.phoneshop.order;

import com.es.phoneshop.order.exceptions.OrderNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ArrayListOrderDao implements OrderDao {
    private static final String ORDER_NULL_MSG = "order == null";

    private static OrderDao orderDao = null;

    private final List<Order> orders;
    private final ReadWriteLock lock;

    private ArrayListOrderDao() {
        orders = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }

    public static synchronized OrderDao getInstance() {
        if (orderDao == null) {
            orderDao = new ArrayListOrderDao();
        }
        return orderDao;
    }


    public int getSize() {
        lock.readLock().lock();
        try {
            return orders.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Order getOrder(String id) throws OrderNotFoundException {
        lock.readLock().lock();
        try {
            if (id == null) {
                throw new OrderNotFoundException();
            }
            return orders
                    .stream()
                    .filter(p -> id.equals(p.getSecureId()))
                    .findAny()
                    .orElseThrow(OrderNotFoundException::new);
        } finally {
            lock.readLock().unlock();
        }
    }


    @Override
    public void save(Order order) {
        if (order == null) {
            throw new IllegalArgumentException(ORDER_NULL_MSG);
        }
        addToList(order);
    }

    private void addToList(Order order) {
        lock.writeLock().lock();
        try {
            if (order.getSecureId() != null) {
                orders.add(order);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public synchronized void deleteAll() {
        orders.clear();
    }
}
