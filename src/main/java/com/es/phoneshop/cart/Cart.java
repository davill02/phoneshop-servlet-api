package com.es.phoneshop.cart;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    List<CartItem> items;

    public Cart(){
        items = new ArrayList<CartItem>();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
