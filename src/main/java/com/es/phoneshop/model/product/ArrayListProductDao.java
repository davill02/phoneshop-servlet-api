package com.es.phoneshop.model.product;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayListProductDao implements ProductDao {
    private final List<Product> products;
    private Long nextId = 0L;
    private final ReadWriteLock lock;
    private static final double EPS = 10E-13;

    public ArrayListProductDao() {
        products = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }

    public void deleteAll() {
        lock.writeLock().lock();
        try {
            products.clear();
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
        Comparator<Product> comparator;
        try {
            Stream<Product> productStream = products.stream()
                    .filter(p -> query == null || query.trim().isEmpty()
                            || relevantStrings(p.getDescription(), strings) > 0)
                    .filter(p -> p.getId() != null)
                    .filter(p -> p.getStock() > 0)
                    .filter(p -> p.getPrice() != null)
                    .sorted(Comparator.comparing((Product p) -> relevantStrings(p.getDescription(), strings)));
            if (sortField != null && sortOrder != null) {
                comparator = Comparator.comparing(p -> {
                    if (sortField == SortField.description) {
                        return (Comparable) p.getDescription();
                    } else {
                        return (Comparable) p.getPrice();
                    }
                });
                if (sortOrder == SortOrder.desc){
                   comparator = comparator.reversed();
                }
                productStream = productStream.sorted(comparator);
            }
            return productStream.collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
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
            result = str.trim().chars().filter(c -> c == (int) ' ').count();
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


    public void saveDefaultProducts() {

        Currency usd = Currency.getInstance("USD");
        save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));

    }
}
