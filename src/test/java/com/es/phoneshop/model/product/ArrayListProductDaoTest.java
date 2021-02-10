package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArrayListProductDaoTest {
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
    private ProductDao productDao;
    private final Currency usd = Currency.getInstance(CURRENCY_CODE);
    private Product notDefaultProduct;

    @Before
    public void setup() {
        notDefaultProduct = new Product(CODE, DESCRIPTION, PRICE, usd, STOCK, IMAGE_URL);
        productDao = new ArrayListProductDao();
        productDao.saveDefaultProducts();
    }

    @Test
    public void shouldFindProductsDefaultProducts() {
        List<Product> result = productDao.findProducts();
        
        assertEquals(DEFAULT_SIZE_WITH_NOT_NULL_PRICE_AND_NOT_ZERO_STOCK, result.size());
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

        int expected  = productDao.getProducts().size();
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetSizeSaveProduct() {
        productDao.save(notDefaultProduct);

        int result = productDao.getSize();

        int expected  = productDao.getProducts().size();
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetSizeDeleteProduct() {
        productDao.delete(EXIST_ID);

        int result = productDao.getSize();

        assertEquals(productDao.getProducts().size(), result);
    }

    @Test
    public void shouldFindProductById(){
        Product result = productDao.getProduct(EXIST_ID);

        assertEquals((Long)EXIST_ID,result.getId());
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldFindNonexistProduct(){
        productDao.getProduct(NON_EXIST_ID);
    }


    @Test
    public void shouldDeleteAll() {
        productDao.deleteAll();

        int result = productDao.getSize();
        Long resultStartId = productDao.getNextId();
        int expected = productDao.getProducts().size();
        assertEquals(expected, result);
        assertEquals(DEFAULT_ID,resultStartId);
    }

    @Test
    public void shouldFindProductsNoResultsByStock() {
        productDao.deleteAll();
        notDefaultProduct.setStock(ZERO_STOCK);
        productDao.save(notDefaultProduct);

        List<Product> result = productDao.findProducts();

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFindProductsNoResultsById() {
        productDao.deleteAll();
        productDao.save(notDefaultProduct);
        notDefaultProduct.setId(null);

        List<Product> result = productDao.findProducts();

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldFindProductsNoResultsByPrice() {
        productDao.deleteAll();
        notDefaultProduct.setPrice(null);
        productDao.save(notDefaultProduct);

        List<Product> result = productDao.findProducts();

        assertTrue(result.isEmpty());
    }
}
