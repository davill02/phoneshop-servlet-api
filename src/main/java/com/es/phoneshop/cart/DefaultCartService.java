package com.es.phoneshop.cart;

import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


public class DefaultCartService implements CartService {
    public static final String ATTR_CART = "cart";

    private static DefaultCartService cartService;
    private ProductDao productDao;

    private DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static CartService getInstance() {
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
        //TODO Can I throw NullPointerException if id equals null?
        if (productId != null) {
            Product product = productDao.getProduct(productId);
            CartItem item = findCartItem(cart, productId);
            addCartItemToCart(cart, quantity, product, item);
        }
    }

    private void addCartItemToCart(@NotNull Cart cart, int quantity, @NotNull Product product, CartItem item) throws OutOfStockException {
        if (item == null) {
            if (product.getStock() < quantity) {
                throw new OutOfStockException(product.getDescription(), quantity, product.getStock());
            }
            item = new CartItem(product, quantity);
            cart.getItems().add(item);
        } else {
            if (product.getStock() < quantity + item.getQuantity()) {
                throw new OutOfStockException(product.getDescription(), quantity + item.getQuantity(), product.getStock());
            }
            item.setQuantity(item.getQuantity() + quantity);
        }
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
        if (request != null && request.getSession() != null) {
            cart = (Cart) request.getSession().getAttribute(ATTR_CART);
            if (cart == null) {
                cart = new Cart();
                request.getSession().setAttribute(ATTR_CART, cart);
            }
        } else {
            //TODO return null or return new Cart?
            cart = new Cart();
        }
        return cart;
    }
}
