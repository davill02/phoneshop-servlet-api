package com.es.phoneshop.order;

import com.es.phoneshop.cart.Cart;

import java.util.List;

public interface OrderService {
    Order getOrder(Cart cart);
    void placeOrder(Order order);
    List<PaymentType> getPaymentTypes();
}
