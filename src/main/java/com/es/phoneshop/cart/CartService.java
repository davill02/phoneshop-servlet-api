package com.es.phoneshop.cart;

public interface CartService {
    void add(Long productId, int quantity);

    void delete(Long productId, int quantity);

    Cart getCart();
}
