package com.es.phoneshop.cart;

import com.es.phoneshop.model.product.Product;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    List<Product> items;

    public Cart(){
        items = new ArrayList<Product>();
    }

    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }
}
