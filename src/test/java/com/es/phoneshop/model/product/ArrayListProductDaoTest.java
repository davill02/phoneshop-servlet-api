package com.es.phoneshop.model.product;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        productDao = new ArrayListProductDao();
        productDao.saveDefaultProducts();
    }

    @Test
    public void shouldFindProductsSgs3First(){
        List<Product> products = productDao.findProducts("Samsung Galaxy S III",null,null);

        assertEquals("sgs3",products.get(0).getCode());
        assertEquals("sgs",products.get(1).getCode());
    }
    @Test
    public void shouldFindProductSgsFirst(){
        List<Product> products = productDao.findProducts("Samsung Galaxy S", null,null);

        assertEquals("sgs",products.get(0).getCode());
        assertEquals("sgs3",products.get(1).getCode());
    }
    @Test
    public void shouldFindProductsTwoProducts(){
        List<Product> products = productDao.findProducts("Palm Pixi Nokia 3310",null,null);

        assertEquals("nokia3310",products.get(0).getCode());
        assertEquals("palmp",products.get(1).getCode());
        assertEquals(2,products.size());
    }
    @Test
    public void shouldFindProductsDefaultProducts() {
        assertEquals(12, productDao.findProducts("",null,null).size());
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldDeleteProduct() {
        productDao.delete(1L);

        assertEquals(11, productDao.findProducts("",null,null).size());
        productDao.getProduct(1L);
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldSaveNull() {
        productDao.save(null);
    }

    @Test
    public void shouldProductWithNullId() {
        notDefaultProduct.setId(null);

        productDao.save(notDefaultProduct);

        assertEquals(13, productDao.findProducts("",null,null).size());
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
    public void shouldFindProductsWithNullQuery(){
        List<Product> products = productDao.findProducts(null,null,null);

        assertEquals(12,products.size());
    }
    @Test
    public void shouldFindProductsAscByPrice(){
        List<Product> products = productDao.findProducts("",SortField.price,SortOrder.asc);

        assertEquals("nokia3310",products.get(0).getCode());
        assertEquals("iphone6",products.get(11).getCode());
    }
    @Test
    public void shouldFindProductsDescByPrice(){
        List<Product> products = productDao.findProducts("",SortField.price,SortOrder.desc);

        assertEquals("iphone6",products.get(0).getCode());
        assertEquals("simc56",products.get(11).getCode());
    }
    @Test
    public void shouldFindProductsAscByDescription(){
        List<Product> products = productDao.findProducts("",SortField.description,SortOrder.asc);

        assertEquals("iphone",products.get(0).getCode());
        assertEquals("xperiaxz",products.get(11).getCode());
    }
    @Test
    public void shouldFindProductsDescByDescription(){
        List<Product> products = productDao.findProducts("",SortField.description,SortOrder.desc);

        assertEquals("xperiaxz",products.get(0).getCode());
        assertEquals("iphone",products.get(11).getCode());
    }

    @Test
    public void shouldFindProductsNoResultsByStock() {
        productDao.deleteAll();
        productDao.save(notDefaultProduct);
        notDefaultProduct.setStock(0);

        assertTrue(productDao.findProducts("",null,null).isEmpty());
    }

    @Test
    public void shouldFindProductsNoResultsById() {
        productDao.deleteAll();
        productDao.save(notDefaultProduct);
        notDefaultProduct.setId(null);

        assertTrue(productDao.findProducts("",null,null).isEmpty());
    }

    @Test
    public void shouldFindProductsNoResultsByPrice() {
        productDao.deleteAll();
        productDao.save(notDefaultProduct);
        notDefaultProduct.setPrice(null);

        assertTrue(productDao.findProducts("",null,null).isEmpty());
    }
}
