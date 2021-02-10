package com.es.phoneshop.cart.exceptions;

public class InvalidQuantityException extends Exception {

    private static final String EXCEPTION_MSG = "Illegal quantity: ";

    public InvalidQuantityException(int quantity) {
        super(EXCEPTION_MSG + quantity);
    }
}
