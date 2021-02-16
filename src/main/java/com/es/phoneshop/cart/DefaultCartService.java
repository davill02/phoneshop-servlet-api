package com.es.phoneshop.cart;

import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static com.es.phoneshop.web.ServletsConstants.USD;


public class DefaultCartService implements CartService {

    private static DefaultCartService cartService;
    private ProductDao productDao;

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static synchronized CartService getInstance() {
        if (cartService == null) {
            cartService = new DefaultCartService();
        }
        return cartService;
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws ProductNotFoundException, OutOfStockException, InvalidQuantityException {
        if (quantity <= 0) {
            throw new InvalidQuantityException(quantity);
        }
        if (productId != null) {
            Product product = productDao.getProduct(productId);
            CartItem item = findCartItem(cart, productId);
            addCartItemToCart(cart, quantity, product, item);
            recalculateTotalPrice(cart);
        }
    }


    private void recalculateTotalPrice(Cart cart) {
        BigDecimal cartTotalPrice = cart
                .getItems()
                .stream()
                .map(cartItem -> cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(new BigDecimal(0));
        cart.setTotalPrice(cartTotalPrice);
    }

    private void addCartItemToCart(@NotNull Cart cart, int quantity, @NotNull Product product, CartItem item) throws OutOfStockException, InvalidQuantityException {
        if (item == null) {
            addIfNotContainsInCart(quantity, product, cart);
        } else {
            setQuantity(quantity + item.getQuantity(), item, product);
        }
    }

    private void addIfNotContainsInCart(int quantity, @NotNull Product product, @NotNull Cart cart) throws OutOfStockException {
        CartItem item;
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product.getDescription(), quantity, product.getStock());
        }
        item = new CartItem(product, quantity);
        cart.getItems().add(item);
    }

    @Nullable
    private CartItem findCartItem(@NotNull Cart cart, @NotNull Long productId) {
        return cart
                .getItems()
                .stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findAny()
                .orElse(null);
    }

    @Override
    public void delete(Long productId, int quantity) {
        //TODO
    }

    @Override
    public Cart getCart(HttpServletRequest request) {
        Cart cart;
        if (request != null) {
            cart = (Cart) request.getSession().getAttribute(ATTR_CART);
            if (cart == null) {
                cart = new Cart();
                cart.setCurrency(USD);
                request.getSession().setAttribute(ATTR_CART, cart);
            }
        } else {
            cart = new Cart();
            cart.setCurrency(USD);
        }

        return cart;
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException, ProductNotFoundException, InvalidQuantityException {
        CartItem item = null;
        if (cart != null && productId != null) {
            item = findCartItem(cart, productId);
        }
        Product product = productDao.getProduct(productId);
        if (item != null) {
            setQuantity(quantity, item, product);
        }
    }

    private void setQuantity(int quantity, @NotNull CartItem item, @NotNull Product product) throws OutOfStockException, InvalidQuantityException {
        if(quantity <= 0){
            throw new InvalidQuantityException(quantity);
        }
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product.getDescription(), quantity, product.getStock());
        } else {
            item.setQuantity(quantity);
        }
    }
}
