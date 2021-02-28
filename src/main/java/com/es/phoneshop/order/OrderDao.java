package com.es.phoneshop.order;

public interface OrderDao {
    Order getOrder(String id);
    void save(Order order);
}
