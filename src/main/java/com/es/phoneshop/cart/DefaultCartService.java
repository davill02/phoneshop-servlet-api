package com.es.phoneshop.cart;

import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;


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
        }
    }

    private void addCartItemToCart(@NotNull Cart cart, int quantity, @NotNull Product product, CartItem item) throws OutOfStockException {
        if (item == null) {
            addIfNotContainsInCart(quantity, product, cart);
        } else {
            addIfContainsInCart(quantity, product, item);
        }
    }

    private void addIfContainsInCart(int quantity, @NotNull Product product, CartItem item) throws OutOfStockException {
        if (product.getStock() < quantity + item.getQuantity()) {
            throw new OutOfStockException(product.getDescription(), quantity + item.getQuantity(), product.getStock());
        }
        item.setQuantity(item.getQuantity() + quantity);
    }

    private void addIfNotContainsInCart(int quantity, @NotNull Product product, @NotNull Cart cart) throws OutOfStockException {
        CartItem item;
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product.getDescription(), quantity, product.getStock());
        }
        item = new CartItem(product, quantity);
        cart.getItems().add(item);
    }

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
                request.getSession().setAttribute(ATTR_CART, cart);
            }
        } else {
            cart = new Cart();
        }
        return cart;
    }
}
