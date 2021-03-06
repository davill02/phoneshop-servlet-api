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
import java.util.ArrayList;
import java.util.List;

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
    public void add(Cart cart, Long productId, int quantity)
            throws ProductNotFoundException, OutOfStockException, InvalidQuantityException {
        if (quantity <= 0) {
            throw new InvalidQuantityException(quantity);
        }
        if (cart != null && productId != null) {
            synchronized (cart) {
                Product product = productDao.getProduct(productId);
                CartItem item = findCartItem(cart, productId);
                addCartItemToCart(cart, quantity, product, item);
                recalculateCart(cart);
            }
        }
    }

    private void recalculateCart(Cart cart) {
        recalculateTotalPrice(cart);
        recalculateTotalQuantity(cart);
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

    private void recalculateTotalQuantity(Cart cart) {
        int result = cart.getItems()
                .stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        cart.setTotalQuantity(result);
    }

    private void addCartItemToCart(@NotNull Cart cart, int quantity, @NotNull Product product, CartItem item)
            throws OutOfStockException, InvalidQuantityException {
        if (item == null) {
            addIfNotContainsInCart(quantity, product, cart);
        } else {
            addIfContainsInCart(quantity + item.getQuantity(), product, item);
        }
    }

    private void addIfNotContainsInCart(int quantity, @NotNull Product product, @NotNull Cart cart)
            throws OutOfStockException {
        CartItem item;
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product.getDescription(), quantity, product.getStock());
        }
        item = new CartItem(product, quantity);
        cart.getItems().add(item);
    }

    private void addIfContainsInCart(int quantity, @NotNull Product product, @NotNull CartItem item)
            throws OutOfStockException, InvalidQuantityException {
        if (quantity <= 0) {
            throw new InvalidQuantityException(quantity);
        }
        if (product.getStock() < quantity) {
            throw new OutOfStockException(product.getDescription(), quantity, product.getStock());
        } else {
            item.setQuantity(quantity);
        }
    }


    @Nullable
    private CartItem findCartItem(@NotNull Cart cart, @NotNull Long productId) {
        return cart
                .getItems()
                .stream()
                .filter(cartItem -> productId.equals(cartItem.getProduct().getId()))
                .findAny()
                .orElse(null);
    }

    @Override
    public void delete(Cart cart, Long productId) {
        if (cart != null && productId != null) {
            synchronized (cart) {
                CartItem item = findCartItem(cart, productId);
                removeItem(cart, item);
                recalculateCart(cart);
            }
        }
    }

    private void removeItem(Cart cart, CartItem item) {
        if (item != null) {
            cart.getItems().remove(item);
        }
    }

    @Override
    public synchronized Cart getCart(HttpServletRequest request) {
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
    public void update(Cart cart, Long productId, int quantity)
            throws OutOfStockException, ProductNotFoundException, InvalidQuantityException {
        if (cart != null && productId != null) {
            synchronized (cart) {
                CartItem item = findCartItem(cart, productId);
                Product product = productDao.getProduct(productId);
                if (item != null) {
                    addIfContainsInCart(quantity, product, item);
                }
                recalculateCart(cart);
            }
        }

    }


    public synchronized void normalizeCart(Cart cart) {
        if (cart != null) {
            List<Long> deletingProductIds = new ArrayList<>();
            cart.getItems().forEach(cartItem -> {
                try {
                    Product product = productDao.getProduct(cartItem.getProduct().getId());
                    normalizeStock(cartItem, product, deletingProductIds);
                } catch (ProductNotFoundException e) {
                    deletingProductIds.add(cartItem.getProduct().getId());
                }
            });
            for (Long id : deletingProductIds) {
                delete(cart, id);
            }
            recalculateCart(cart);
        }
    }

    private void normalizeStock(CartItem cartItem, Product product, List<Long> deletingProductIds) {
        if (product.getStock() < cartItem.getQuantity()) {
            cartItem.setQuantity(product.getStock());
        }
        if (product.getStock() == 0) {
            deletingProductIds.add(product.getId());
        }
    }
}
