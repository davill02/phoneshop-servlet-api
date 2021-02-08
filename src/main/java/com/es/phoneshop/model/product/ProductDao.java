package com.es.phoneshop.model.product;

import com.es.phoneshop.search.emuns.SortField;
import com.es.phoneshop.search.emuns.SortOrder;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id);

    List<Product> findProducts();

    void save(Product product);

    void delete(Long id);

    void deleteAll();

    int getSize();

    Long getNextId();

    List<Product> getProducts();
}
