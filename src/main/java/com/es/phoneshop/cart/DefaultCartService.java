package com.es.phoneshop.cart;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

public class DefaultCartService implements CartService {
    private static DefaultCartService cartService;
    private ProductDao productDao;
    private Cart cart;
    public static CartService getInstance(){
        if(cartService == null){
            cartService = new DefaultCartService();
        }
        return cartService;
    }
    private DefaultCartService(){
        cart = new Cart();
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    public void add(Long productId, int quantity) {
        Product product = productDao.getProduct(productId);
        cart.getItems().add(product);
    }

    @Override
    public void delete(Long productId, int quantity) {

    }

    @Override
    public Cart getCart() {
        return cart;
    }
}
