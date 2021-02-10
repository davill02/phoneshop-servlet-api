package com.es.phoneshop.model.product;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private static final String PRODUCT_NULL_MSG = "product == null";
    private static final long DEFAULT_ID = 0L;
    private static ProductDao productDao = null;

    private final List<Product> products;
    private Long nextId = DEFAULT_ID;
    private final ReadWriteLock lock;

    private ArrayListProductDao() {
        products = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }

    public static synchronized ProductDao getInstance() {
        if (productDao == null) {
            productDao = new ArrayListProductDao();
        }
        return productDao;
    }

    public List<Product> getProducts() {
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(products);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Long getNextId() {
        lock.readLock().lock();
        try {
            return nextId;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void deleteAll() {
        lock.writeLock().lock();
        try {
            products.clear();
            nextId = DEFAULT_ID;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getSize() {
        lock.readLock().lock();
        try {
            return products.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Product getProduct(Long id) throws NoSuchElementException {
        lock.readLock().lock();
        try {
            return products
                    .stream()
                    .filter(p -> id.equals(p.getId()))
                    .findAny()
                    .get();
        } finally {
            lock.readLock().unlock();
        }

    }

    @Override
    public List<Product> findProducts() {
        return getProducts();
    }

    @Override
    public void save(Product product) {
        if (product == null) {
            throw new NoSuchElementException(PRODUCT_NULL_MSG);
        }
        lock.writeLock().lock();
        try {
            if (product.getId() != null && !product.getId().equals(DEFAULT_ID)) {
                handleProductWithId(product);
            } else {
                nextId++;
                product.setId(nextId);
                products.add(product);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void handleProductWithId(@NotNull Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (product.getId().equals(products.get(i).getId())) {
                products.set(i, product);
                break;
            }
            if (i == products.size() - 1) {
                products.add(product);
            }
        }
    }

    @Override
    public void delete(Long id) {
        lock.writeLock().lock();
        try {
            if (id != null) {
                removeProduct(id);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void removeProduct(@NotNull Long id) {
        for (int i = 0; i < products.size(); i++) {
            if (id.equals(products.get(i).getId())) {
                products.remove(i);
                break;
            }
        }
    }
}
