package com.es.phoneshop.cart;

import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.exceptions.ProductNotFoundException;

import javax.servlet.http.HttpServletRequest;

public interface CartService {
    void add(Cart cart, Long productId, int quantity) throws ProductNotFoundException, OutOfStockException, InvalidQuantityException;

    void delete(Long productId, int quantity);

    Cart getCart(HttpServletRequest request);
}
