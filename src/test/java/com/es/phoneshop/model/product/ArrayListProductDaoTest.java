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
    private ProductDao productDao;
    private Currency usd;
    private Product notDefaultProduct;

    @Before
    public void setup() {
        notDefaultProduct = new Product("iphone7", "IPhone 7", new BigDecimal(1200), usd, 9, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg");
        usd = Currency.getInstance("USD");
        productDao = ArrayListProductDao.getInstance();
        productDao.deleteAll();
        productDao.save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        productDao.save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        productDao.save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        productDao.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        productDao.save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        productDao.save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        productDao.save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        productDao.save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        productDao.save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        productDao.save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        productDao.save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));

    }

    @Test
    public void shouldFindProductsSgs3First() {
        List<Product> products = productDao.findProducts("Samsung Galaxy S III", null, null);

        assertEquals("sgs3", products.get(0).getCode());
        assertEquals("sgs", products.get(1).getCode());
    }

    @Test
    public void shouldFindProductSgsFirst() {
        List<Product> products = productDao.findProducts("Samsung Galaxy S", null, null);

        assertEquals("sgs", products.get(0).getCode());
        assertEquals("sgs3", products.get(1).getCode());
    }

    @Test
    public void shouldFindProductsTwoProducts() {
        List<Product> products = productDao.findProducts("Palm Pixi Nokia 3310", null, null);

        assertEquals("nokia3310", products.get(0).getCode());
        assertEquals("palmp", products.get(1).getCode());
        assertEquals(2, products.size());
    }

    @Test
    public void shouldFindProductsDefaultProducts() {
        assertEquals(12, productDao.findProducts("", null, null).size());
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldDeleteProduct() {
        Long id = 2L;

        productDao.delete(id);

        assertEquals(11, productDao.findProducts("", null, null).size());
        productDao.getProduct(id);
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldSaveNull() {
        productDao.save(null);
    }

    @Test
    public void shouldProductWithNullId() {
        notDefaultProduct.setId(null);

        productDao.save(notDefaultProduct);

        assertEquals(13, productDao.findProducts("", null, null).size());
    }

    @Test
    public void shouldSaveNewProduct() {
        productDao.save(notDefaultProduct);

        assertEquals(14, productDao.getSize());
        assertEquals("iphone7", productDao.getProduct(14L).getCode());
    }

    @Test
    public void shouldSaveUpdateProduct() {
        Long existId = 1L;
        notDefaultProduct.setId(existId);

        productDao.save(notDefaultProduct);

        assertEquals(notDefaultProduct, productDao.getProduct(existId));
        assertEquals("iphone7", productDao.getProduct(existId).getCode());
    }

    @Test
    public void shouldSaveProductWithId() {
        Long id = 17L;
        notDefaultProduct.setId(id);

        productDao.save(notDefaultProduct);

        assertEquals(notDefaultProduct, productDao.getProduct(id));
    }

    @Test
    public void shouldGetSize() {
        assertEquals(13, productDao.getSize());
    }

    @Test
    public void shouldGetSizeSaveProduct() {
        productDao.save(notDefaultProduct);

        assertEquals(14, productDao.getSize());
    }

    @Test
    public void shouldGetSizeDeleteProduct() {
        productDao.delete(1L);

        assertEquals(12, productDao.getSize());
    }


    @Test
    public void shouldDeleteAll() {
        productDao.deleteAll();

        assertEquals(0, productDao.getSize());
    }

    @Test
    public void shouldFindProductsWithNullQuery() {
        List<Product> products = productDao.findProducts(null, null, null);

        assertEquals(12, products.size());
    }

    @Test
    public void shouldFindProductsAscByPrice() {
        List<Product> products = productDao.findProducts("", SortField.price, SortOrder.asc);

        assertEquals("nokia3310", products.get(0).getCode());
        assertEquals("iphone6", products.get(11).getCode());
    }

    @Test
    public void shouldFindProductsDescByPrice() {
        List<Product> products = productDao.findProducts("", SortField.price, SortOrder.desc);

        assertEquals("iphone6", products.get(0).getCode());
        assertEquals("simc56", products.get(11).getCode());
    }

    @Test
    public void shouldFindProductsAscByDescription() {
        List<Product> products = productDao.findProducts("", SortField.description, SortOrder.asc);

        assertEquals("iphone", products.get(0).getCode());
        assertEquals("xperiaxz", products.get(11).getCode());
    }

    @Test
    public void shouldFindProductsDescByDescription() {
        List<Product> products = productDao.findProducts("", SortField.description, SortOrder.desc);

        assertEquals("xperiaxz", products.get(0).getCode());
        assertEquals("iphone", products.get(11).getCode());
    }

    @Test
    public void shouldFindProductsNoResultsByStock() {
        productDao.deleteAll();
        productDao.save(notDefaultProduct);
        notDefaultProduct.setStock(0);

        assertTrue(productDao.findProducts("", null, null).isEmpty());
    }

    @Test
    public void shouldFindProductsNoResultsById() {
        productDao.deleteAll();
        productDao.save(notDefaultProduct);
        notDefaultProduct.setId(null);

        assertTrue(productDao.findProducts("", null, null).isEmpty());
    }

    @Test
    public void shouldFindProductsNoResultsByPrice() {
        productDao.deleteAll();
        productDao.save(notDefaultProduct);
        notDefaultProduct.setPrice(null);

        assertTrue(productDao.findProducts("", null, null).isEmpty());
    }
}
