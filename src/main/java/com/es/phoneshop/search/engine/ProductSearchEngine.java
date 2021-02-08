package com.es.phoneshop.search.engine;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.search.emuns.SortField;
import com.es.phoneshop.search.emuns.SortOrder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductSearchEngine implements SearchEngine<Product> {
    private static final char SPACE = ' ';
    private static final int ZERO_STOCK = 0;
    private static final String EMPTY_STRING = "";

    private List<Product> productList;

    public ProductSearchEngine(List<Product> productList) {
        this.productList = productList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public List<Product> search() {
        return standardFilters(productList.stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> search(SortField field, SortOrder order) {
        Stream<Product> productStream = standardFilters(productList.stream());
        return sortingByParameters(productStream, field, order)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> search(String query, SortField sortField, SortOrder sortOrder) {
        Stream<Product> productStream = standardFilters(productList.stream());
        productStream = filterAndSortByQuery(productStream, query);
        return sortingByParameters(productStream, sortField, sortOrder)
                .collect(Collectors.toList());

    }

    private Stream<Product> filterAndSortByQuery(Stream<Product> productStream, String query) {
        List<String> strings = divideIntoWords(query);
        return productStream
                .filter(p -> query == null || query.trim().isEmpty() || relevantStrings(p.getDescription(), strings) > 0)
                .sorted(Comparator.comparing((Product p) -> relevantStrings(p.getDescription(), strings)));
    }

    private Stream<Product> standardFilters(Stream<Product> productStream) {
        return productStream
                .filter(p -> p.getId() != null)
                .filter(p -> p.getStock() > ZERO_STOCK)
                .filter(p -> p.getPrice() != null);
    }

    private Stream<Product> sortingByParameters(Stream<Product> productStream, SortField sortField, SortOrder sortOrder) {
        if (sortField != null && sortOrder != null) {
            productStream = productStream.sorted(getProductComparator(sortField, sortOrder));
        }
        return productStream;
    }

    @NotNull
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

    private List<String> divideIntoWords(String query) {
        String tempQuery;
        List<String> results = new ArrayList<>();
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
}
