package com.es.phoneshop.search.engine;


import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.search.emuns.SortField;
import com.es.phoneshop.search.emuns.SortOrder;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ProductSearchEngineTest {
    public static final String SAMSUNG_GALAXY_S_III = "Samsung Galaxy S III";
    private static final String CODE = "iphone7";
    private static final String DESCRIPTION = "IPhone 7";
    private static final BigDecimal PRICE = new BigDecimal(1200);
    private static final int STOCK = 9;
    private static final String IMAGE_URL = "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg";
    private static final int DEFAULT_SIZE_WITH_NOT_NULL_PRICE_AND_NOT_ZERO_STOCK = 12;
    private static final int ZERO_STOCK = 0;
    private static final String EMPTY_QUERY = "";
    private static final String CURRENCY_CODE = "USD";

    private Product notDefaultProduct;
    private SearchEngine<Product> searchEngine;
    private List<Product> defaultProducts;

    @Before
    public void setup() {
        Currency usd = Currency.getInstance(CURRENCY_CODE);
        defaultProducts = new ArrayList<>();
        notDefaultProduct = new Product(CODE, DESCRIPTION, PRICE, usd, STOCK, IMAGE_URL);

        defaultProducts.add(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        defaultProducts.add(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        defaultProducts.add(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        defaultProducts.add(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        defaultProducts.add(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        defaultProducts.add(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        defaultProducts.add(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        defaultProducts.add(new Product("xperiaz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        defaultProducts.add(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        defaultProducts.add(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        defaultProducts.add(new Product("smc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        defaultProducts.add(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        defaultProducts.add(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));

        searchEngine = new ProductSearchEngine(defaultProducts);
    }

    @Test
    public void shouldFindProductsDefaultProducts() {
        List<Product> result = searchEngine.search(EMPTY_QUERY, null, null);

        assertEquals(DEFAULT_SIZE_WITH_NOT_NULL_PRICE_AND_NOT_ZERO_STOCK, result.size());
    }

    @Test
    public void shouldSearchDefaultProductsWithoutArgs() {
        List<Product> result = searchEngine.search();

        assertEquals(DEFAULT_SIZE_WITH_NOT_NULL_PRICE_AND_NOT_ZERO_STOCK, result.size());
    }

    @Test
    public void shouldFindProductsWithNullQuery() {
        List<Product> products = searchEngine.search(null, null, null);

        assertEquals(DEFAULT_SIZE_WITH_NOT_NULL_PRICE_AND_NOT_ZERO_STOCK, products.size());
    }

    @Test
    public void shouldFindProductsAscByPrice() {
        List<BigDecimal> expected = defaultProducts
                .stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .map(Product::getPrice)
                .sorted()
                .collect(Collectors.toList());

        List<Product> products = searchEngine.search(EMPTY_QUERY, SortField.PRICE, SortOrder.ASC);
        List<BigDecimal> result = products
                .stream()
                .map(Product::getPrice)
                .collect(Collectors.toList());

        assertEquals(expected, result);
    }

    @Test
    public void shouldFindProductsDescByPrice() {
        List<BigDecimal> expected = defaultProducts
                .stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .map(Product::getPrice)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        List<Product> products = searchEngine.search(EMPTY_QUERY, SortField.PRICE, SortOrder.DESC);
        List<BigDecimal> result = products
                .stream()
                .map(Product::getPrice)
                .collect(Collectors.toList());

        assertEquals(expected, result);
    }

    @Test
    public void shouldFindProductsAscByDescription() {
        List<String> expected = defaultProducts
                .stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .map(Product::getDescription)
                .sorted()
                .collect(Collectors.toList());

        List<Product> products = searchEngine.search(EMPTY_QUERY, SortField.DESCRIPTION, SortOrder.ASC);
        List<String> result = products
                .stream()
                .map(Product::getDescription)
                .collect(Collectors.toList());

        assertEquals(expected, result);
    }

    @Test
    public void shouldFindProductsDescByDescription() {
        List<String> expected = defaultProducts
                .stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .map(Product::getDescription)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        List<Product> products = searchEngine.search(EMPTY_QUERY, SortField.DESCRIPTION, SortOrder.DESC);
        List<String> result = products
                .stream()
                .map(Product::getDescription)
                .collect(Collectors.toList());

        assertEquals(expected, result);
    }

    @Test
    public void shouldSearchDescByDescriptionWithoutQuery() {
        List<String> expected = defaultProducts
                .stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .map(Product::getDescription)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        List<Product> products = searchEngine.search(SortField.DESCRIPTION, SortOrder.DESC);
        List<String> result = products
                .stream()
                .map(Product::getDescription)
                .collect(Collectors.toList());

        assertEquals(expected, result);
    }

    @Test
    public void shouldFindProductsNoResultsById() {
        List<Product> productList = new ArrayList<>();
        productList.add(notDefaultProduct);
        ((ProductSearchEngine) searchEngine).setProductList(productList);
        notDefaultProduct.setId(null);

        List<Product> result = searchEngine.search(EMPTY_QUERY, null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFindProductsNoResultsByStock() {
        List<Product> productList = new ArrayList<>();
        productList.add(notDefaultProduct);
        ((ProductSearchEngine) searchEngine).setProductList(productList);
        notDefaultProduct.setStock(ZERO_STOCK);

        List<Product> result = searchEngine.search(EMPTY_QUERY, null, null);

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldFindProductsNoResultsByPrice() {
        List<Product> productList = new ArrayList<>();
        productList.add(notDefaultProduct);
        ((ProductSearchEngine) searchEngine).setProductList(productList);
        notDefaultProduct.setPrice(null);

        List<Product> result = searchEngine.search(EMPTY_QUERY, null, null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFindProductByDescription() {
        List<Product> result = searchEngine.search(SAMSUNG_GALAXY_S_III, null, null);

        assertEquals(SAMSUNG_GALAXY_S_III, result.get(0).getDescription());
    }

    @Test
    public void shouldFindProductByDescriptionInUpperCase() {
        List<Product> result = searchEngine.search(SAMSUNG_GALAXY_S_III.toUpperCase(), null, null);

        assertEquals(SAMSUNG_GALAXY_S_III, result.get(0).getDescription());
    }

}

