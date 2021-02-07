package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArrayListProductDaoTest {
    public static final String SAMSUNG_GALAXY_S_III = "Samsung Galaxy S III";
    private static final String CODE = "iphone7";
    private static final String DESCRIPTION = "IPhone 7";
    private static final BigDecimal PRICE = new BigDecimal(1200);
    private static final int STOCK = 9;
    private static final String IMAGE_URL = "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg";
    private static final int DEFAULT_SIZE_WITH_NOT_NULL_PRICE_AND_NOT_ZERO_STOCK = 12;
    private static final long EXIST_ID = 1L;
    private static final long NON_EXIST_ID = 17L;
    private static final long ZERO_COUNT = 0L;
    private static final long ONE_COUNT = 1L;
    private static final int ZERO_STOCK = 0;
    private static final String CURRENCY_CODE = "USD";
    private static final Long DEFAULT_ID = 0L;
    private static final String EMPTY_QUERY = "";
    private static final String CODE_SIMC_56 = "simc56";
    private static final String CODE_XPERIAXZ = "xperiaxz";
    private ProductDao productDao;
    private Product notDefaultProduct;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        productDao.deleteAll();
        Currency usd = Currency.getInstance(CURRENCY_CODE);
        productDao.save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        productDao.save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        productDao.save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        productDao.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        productDao.save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        productDao.save(new Product(CODE_XPERIAXZ, "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        productDao.save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        productDao.save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        productDao.save(new Product(CODE_SIMC_56, "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        productDao.save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        productDao.save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        notDefaultProduct = new Product(CODE, DESCRIPTION, PRICE, usd, STOCK, IMAGE_URL);
    }

    @Test
    public void shouldFindProductsDefaultProducts() {
        List<Product> result = productDao.findProducts(EMPTY_QUERY, null, null);

        assertEquals(DEFAULT_SIZE_WITH_NOT_NULL_PRICE_AND_NOT_ZERO_STOCK, result.size());
    }

    @Test
    public void shouldFindProductById(){
        Product result = productDao.getProduct(EXIST_ID);

        assertEquals((Long)EXIST_ID,result.getId());
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFindNonExistProduct(){
        productDao.getProduct(NON_EXIST_ID);
    }


    @Test
    public void shouldDeleteProduct() {
        Long id = EXIST_ID;

        productDao.delete(id);

        long count = productDao
                .getProducts()
                .stream()
                .filter(p -> id.equals(p.getId()))
                .count();
        assertEquals(ZERO_COUNT, count);
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldSaveNull() {
        productDao.save(null);
    }

    @Test
    public void shouldProductWithNullId() {
        notDefaultProduct.setId(null);

        productDao.save(notDefaultProduct);

        long count = productDao
                .getProducts()
                .stream()
                .filter(p -> notDefaultProduct.getDescription().equals(p.getDescription()))
                .filter(p -> p.getId() != null)
                .count();
        assertEquals(ONE_COUNT, count);
    }

    @Test
    public void shouldSaveNewProduct() {
        productDao.save(notDefaultProduct);

        long count = productDao
                .getProducts()
                .stream()
                .filter(p -> notDefaultProduct.getDescription().equals(p.getDescription()))
                .count();
        assertEquals(ONE_COUNT, count);
    }

    @Test
    public void shouldSaveUpdateProduct() {
        Long existId = EXIST_ID;
        notDefaultProduct.setId(existId);

        productDao.save(notDefaultProduct);

        Product product = productDao
                .getProducts()
                .stream()
                .findAny()
                .get();
        assertEquals(CODE, product.getCode());
        assertEquals(existId, product.getId());
    }

    @Test
    public void shouldSaveProductWithId() {
        Long id = NON_EXIST_ID;
        notDefaultProduct.setId(id);

        productDao.save(notDefaultProduct);

        Product product = productDao
                .getProducts()
                .stream()
                .filter(p -> id.equals(p.getId()))
                .findAny()
                .get();
        assertEquals(notDefaultProduct, product);
    }

    @Test
    public void shouldGetSize() {
        int result = productDao.getSize();

        int expected = productDao.getProducts().size();
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetSizeSaveProduct() {
        productDao.save(notDefaultProduct);

        int result = productDao.getSize();

        int expected = productDao.getProducts().size();
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetSizeDeleteProduct() {
        productDao.delete(EXIST_ID);

        int result = productDao.getSize();

        assertEquals(DEFAULT_SIZE_WITH_NOT_NULL_PRICE_AND_NOT_ZERO_STOCK, result);
    }


    @Test
    public void shouldDeleteAll() {
        productDao.deleteAll();

        int result = productDao.getSize();
        Long resultStartId = productDao.getNextId();
        int expected = productDao.getProducts().size();
        assertEquals(expected, result);
        assertEquals(DEFAULT_ID, resultStartId);
    }

    @Test
    public void shouldFindProductsWithNullQuery() {
        List<Product> products = productDao.findProducts(null, null, null);

        assertEquals(DEFAULT_SIZE_WITH_NOT_NULL_PRICE_AND_NOT_ZERO_STOCK, products.size());
    }

    @Test
    public void shouldFindProductsAscByPrice() {
        List<Product> products = productDao.findProducts(EMPTY_QUERY, SortField.price, SortOrder.asc);

        List<BigDecimal> expected = productDao
                .getProducts()
                .stream()
                .filter(product -> product.getPrice() != null && product.getStock() > 0)
                .map(Product::getPrice)
                .sorted()
                .collect(Collectors.toList());
        List<BigDecimal> result = products
                .stream()
                .map(Product::getPrice)
                .collect(Collectors.toList());
        assertEquals(expected,result);
    }

    @Test
    public void shouldFindProductsDescByPrice() {
        List<Product> products = productDao.findProducts(EMPTY_QUERY, SortField.price, SortOrder.desc);

        List<BigDecimal> expected = productDao
                .getProducts()
                .stream()
                .filter(product -> product.getPrice()!=null && product.getStock()>0)
                .map(Product::getPrice)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        List<BigDecimal> result = products
                .stream()
                .map(Product::getPrice)
                .collect(Collectors.toList());
        assertEquals(expected,result);
    }

    @Test
    public void shouldFindProductsAscByDescription() {
        List<Product> products = productDao.findProducts(EMPTY_QUERY, SortField.description, SortOrder.asc);

        List<String> expected = productDao
                .getProducts()
                .stream()
                .filter(product -> product.getPrice()!=null && product.getStock()>0)
                .map(Product::getDescription)
                .sorted()
                .collect(Collectors.toList());
        List<String> result = products
                .stream()
                .map(Product::getDescription)
                .collect(Collectors.toList());
        assertEquals(expected,result);
    }

    @Test
    public void shouldFindProductsDescByDescription() {
        List<Product> products = productDao.findProducts(EMPTY_QUERY, SortField.description, SortOrder.desc);

        List<String> expected = productDao
                .getProducts()
                .stream()
                .filter(product -> product.getPrice()!=null && product.getStock()>0)
                .map(Product::getDescription)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        List<String> result = products
                .stream()
                .map(Product::getDescription)
                .collect(Collectors.toList());
        assertEquals(expected,result);
    }

    @Test
    public void shouldFindProductsNoResultsById() {
        productDao.deleteAll();
        productDao.save(notDefaultProduct);
        notDefaultProduct.setId(null);

        List<Product> result = productDao.findProducts(EMPTY_QUERY, null, null);

        assertTrue(result.isEmpty());
    }
    @Test
    public void shouldFindProductsNoResultsByStock() {
        productDao.deleteAll();
        notDefaultProduct.setStock(0);
        notDefaultProduct.setStock(ZERO_STOCK);
        productDao.save(notDefaultProduct);

        List<Product> result = productDao.findProducts(EMPTY_QUERY,null,null);

        assertTrue(result.isEmpty());
    }


    @Test
    public void shouldFindProductsNoResultsByPrice() {
        productDao.deleteAll();
        notDefaultProduct.setPrice(null);
        productDao.save(notDefaultProduct);

        List<Product> result = productDao.findProducts(EMPTY_QUERY,null,null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFindProductByDescription(){
       List<Product> result = productDao.findProducts(SAMSUNG_GALAXY_S_III,null,null);

       assertEquals(SAMSUNG_GALAXY_S_III,result.get(0).getDescription());
    }

    @Test
    public void shouldFindProductByDescriptionInUpperCase(){
        List<Product> result = productDao.findProducts(SAMSUNG_GALAXY_S_III.toUpperCase(),null,null);

        assertEquals(SAMSUNG_GALAXY_S_III,result.get(0).getDescription());
    }

}
