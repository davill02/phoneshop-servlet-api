package com.es.phoneshop.cart.exceptions;

public class OutOfStockException extends Exception {

    private static final String EXCEPTION_MSG = "You ordered %s in quantity of %d, but we have only %d";

    public OutOfStockException(String description, int quantity, int stock) {
        super(String.format(EXCEPTION_MSG, description,quantity,stock));
    }

    public OutOfStockException() {
        super();
    }
}
