package com.es.phoneshop.model.product;

import com.es.phoneshop.model.exceptions.ProductNotFoundException;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id) throws ProductNotFoundException;

    List<Product> findProducts();

    void save(Product product);

    void delete(Long id);

    void deleteAll();

    int getSize();

    Long getNextId();

    List<Product> getProducts();
}
