package com.es.phoneshop.order;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.order.exceptions.InvalidOrderException;

import java.util.List;

public interface OrderService {
    Order getOrder(Cart cart);

    void placeOrder(Order order) throws InvalidOrderException;

    List<PaymentType> getPaymentTypes();

    boolean isAvailableInStock(Cart cart);

    boolean containsOrder(String id);
}
