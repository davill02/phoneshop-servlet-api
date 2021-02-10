package com.es.phoneshop.cart.exceptions;

public class OutOfStockException extends Exception {

    private static final String FIRST_PART_EXCEPTION_MSG = "You ordered ";
    private static final String SECOND_PART_EXCEPTION_MSG = "in quantity of ";
    private static final String THIRD_PART_EXCEPTION_MSG = ", but we have only ";

    public OutOfStockException(String description, int quantity, int stock) {
        super(FIRST_PART_EXCEPTION_MSG + description + " " + SECOND_PART_EXCEPTION_MSG + quantity + THIRD_PART_EXCEPTION_MSG + stock);
    }

    public OutOfStockException() {
        super();
    }
}
