package com.es.phoneshop.model.product;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayListProductDao implements ProductDao {
    private static ProductDao productDao = null;
    private final List<Product> products;
    private Long nextId = 0L;
    private final ReadWriteLock lock;
    private static final double EPS = 10E-13;

    public static synchronized ProductDao getInstance() {
        if (productDao == null) {
            productDao = new ArrayListProductDao();
        }
        return productDao;
    }

    private ArrayListProductDao() {
        products = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }

    public void deleteAll() {
        lock.writeLock().lock();
        try {
            products.clear();
            nextId = 0L;
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
        List<String> strings = strQueryToList(query);
        try {
            Stream<Product> productStream = products.stream()
                    .filter(p -> query == null || query.trim().isEmpty()
                            || relevantStrings(p.getDescription(), strings) > 0)
                    .filter(p -> p.getId() != null)
                    .filter(p -> p.getStock() > 0)
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

    private List<String> strQueryToList(String query) {
        String tempQuery;
        List<String> results = new ArrayList<String>();
        if (query == null) {
            tempQuery = "";
        } else {
            tempQuery = query.trim();
        }
        StringTokenizer tokens = new StringTokenizer(tempQuery);
        while (tokens.hasMoreTokens()) {
            results.add(tokens.nextToken());
        }
        return results;
    }

    private long countWords(String str) {
        long result = 0;
        if (str != null && !str.isEmpty()) {
            result = str
                    .trim()
                    .chars()
                    .filter(c -> c == (int) ' ')
                    .count();
        }
        return result + 1;
    }

    private long relevantStrings(String description, List<String> strings) {
        long count = 0L;
        if (description != null && !description.trim().isEmpty()) {
            for (String i : strings) {
                if (description.contains(i)) {
                    count++;
                }
            }
        }
        long result = 0L;
        if (count != 0) {
            result = countWords(description) + strings.size() - 2 * count + 1;
        }
        return result;
    }

    @Override
    public void save(Product product) {
        if (product == null) {
            throw new NoSuchElementException("product == null");
        }
        lock.writeLock().lock();
        try {
            if (product.getId() != null && !product.getId().equals(0L)) {
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

    private void handleProductWithId(Product product) {
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
                if (id == nextId) {
                    nextId--;
                }
                for (int i = 0; i < products.size(); i++) {
                    if (id.equals(products.get(i).getId())) {
                        products.remove(i);
                        break;
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


}
