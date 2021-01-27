package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;
    private Currency usd;
    private Product notDefaultProduct;
    @Before
    public void setup() {
        notDefaultProduct = new Product("iphone7", "IPhone 7", new BigDecimal(1200), usd, 9, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg");
        usd = Currency.getInstance("USD");
        productDao = new ArrayListProductDao();
        ((ArrayListProductDao)productDao).saveDefaultProducts();
    }

    @Test
    public void testFindProductsDefaultProducts(){
        assertEquals(12,productDao.findProducts().size());
        productDao.getProduct(1L).setId(null);
        assertEquals(11,productDao.findProducts().size());
    }
    @Test(expected = NoSuchElementException.class)
    public void testDeleteProduct(){
        productDao.delete(1L);
        assertEquals(11,productDao.findProducts().size());
        productDao.getProduct(1L);
    }
    @Test
    public void testSaveNewProduct(){
        productDao.save(notDefaultProduct);
        assertEquals(14,((ArrayListProductDao)productDao).getSize());
        assertEquals("iphone7",productDao.getProduct(14L).getCode());
    }
    @Test
    public void testSaveUpdateProduct(){
        Long existId = 1L;
        notDefaultProduct.setId(existId);
        productDao.save(notDefaultProduct);
        assertEquals(notDefaultProduct,productDao.getProduct(existId));
        assertEquals("iphone7",productDao.getProduct(existId).getCode());
    }
    @Test
    public void testSaveProductWithId(){
        Long id  = 17L;
        notDefaultProduct.setId(id);
        productDao.save(notDefaultProduct);
        assertEquals(notDefaultProduct,productDao.getProduct(id));
    }
    @Test
    public  void testGetSize(){
        assertEquals(13,((ArrayListProductDao)productDao).getSize());
        productDao.save(notDefaultProduct);
        assertEquals(14,((ArrayListProductDao)productDao).getSize());
        productDao.delete(1L);
        assertEquals(13,((ArrayListProductDao)productDao).getSize());
        ((ArrayListProductDao) productDao).deleteAll();
        assertEquals(0,((ArrayListProductDao) productDao).getSize());

    }
    @Test
    public void testDeleteAll(){
        ((ArrayListProductDao)productDao).deleteAll();
        assertEquals(0,((ArrayListProductDao) productDao).getSize());
    }
    @Test
    public void testFindProductsNoResults() {
        ((ArrayListProductDao)productDao).deleteAll();
        assertTrue(productDao.findProducts().isEmpty());
        productDao.save(notDefaultProduct);
        notDefaultProduct.setId(null);
        assertTrue(productDao.findProducts().isEmpty());
        notDefaultProduct.setId(1L);
        notDefaultProduct.setStock(0);
        assertTrue(productDao.findProducts().isEmpty());
        notDefaultProduct.setStock(10);
        assertFalse(productDao.findProducts().isEmpty());

    }
}
