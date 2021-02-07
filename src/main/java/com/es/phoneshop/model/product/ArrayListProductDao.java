package com.es.phoneshop.model.product;

import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ArrayListProductDao implements ProductDao {
    private static final String PRODUCT_NULL_MSG = "product == null";
    private static final int ZERO_STOCK = 0;
    private static final long DEFAULT_ID = 0L;
    public static final char SPACE = ' ';
    public static final String EMPTY_STRING = "";
    private static ProductDao productDao = null;
    private final List<Product> products;
    private Long nextId = DEFAULT_ID;
    private final ReadWriteLock lock;

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

    private ArrayListProductDao() {
        products = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
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
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        lock.readLock().lock();
        List<String> strings = queryToList(query);
        try {
            Stream<Product> productStream = products.stream()
                    .filter(p -> query == null || query.trim().isEmpty()
                            || relevantStrings(p.getDescription(), strings) > 0)
                    .filter(p -> p.getId() != null)
                    .filter(p -> p.getStock() > ZERO_STOCK)
                    .filter(p -> p.getPrice() != null)
                    .sorted(Comparator.comparing((Product p) -> relevantStrings(p.getDescription(), strings)));
            if (sortField != null && sortOrder != null) {
                productStream = productStream.sorted(getProductComparator(sortField, sortOrder));
            }
            return productStream.collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    private Comparator<Product> getProductComparator(SortField sortField, SortOrder sortOrder) {
        Comparator<Product> comparator;
        comparator = Comparator.comparing(p -> {
            if (sortField == SortField.description) {
                return (Comparable) p.getDescription();
            } else {
                return (Comparable) p.getPrice();
            }
        });
        if (sortOrder == SortOrder.desc) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    private List<String> queryToList(String query) {
        String tempQuery;
        List<String> results = new ArrayList<String>();
        if (query == null) {
            tempQuery = EMPTY_STRING;
        } else {
            tempQuery = query.trim();
        }
        StringTokenizer tokens = new StringTokenizer(tempQuery);
        while (tokens.hasMoreTokens()) {
            results.add(tokens.nextToken().toLowerCase());
        }
        return results;
    }

    private long countWords(String str) {
        long result = 0;
        if (str != null && !str.isEmpty()) {
            result = str
                    .trim()
                    .chars()
                    .filter(c -> c == (int) SPACE)
                    .count();
        }
        return result + 1;
    }

    private long relevantStrings(String description, List<String> strings) {
        long count = 0L;
        long result = 0L;
        if (description != null && !description.trim().isEmpty()) {
            for (String i : strings) {
                if (description.toLowerCase().contains(i)) {
                    count++;
                }
            }
        }
        if (count != 0L) {
            result = countWords(description) + strings.size() - 2 * count + 1;
        }
        return result;
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
