package com.es.phoneshop.order;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartItem;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.exceptions.ProductNotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.order.exceptions.InvalidOrderException;
import com.es.phoneshop.order.exceptions.OrderNotFoundException;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {
    public static final String DELIVERY_PRICE = "5";

    private static OrderService service;
    private OrderDao orderDao;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ProductDao productDao;

    private DefaultOrderService() {
        orderDao = ArrayListOrderDao.getInstance();
        productDao = ArrayListProductDao.getInstance();
    }

    public static OrderService getInstance() {
        if (service == null) {
            service = new DefaultOrderService();
        }
        return service;
    }

    @Override
    public Order getOrder(Cart cart) {
        Order resultOrder = new Order();
        if (cart != null) {
            setOrderProperties(cart, resultOrder);
        }
        return resultOrder;

    }

    private void setOrderProperties(Cart cart, Order resultOrder) {
        resultOrder.setItems(cart
                .getItems()
                .stream()
                .map(this::getCartItem)
                .collect(Collectors.toUnmodifiableList()));
        resultOrder.setDeliveryPrice(calculateDeliveryPrice());
        resultOrder.setSubPrice(cart.getTotalPrice());
        resultOrder.setTotalPrice(calculateDeliveryPrice().add(cart.getTotalPrice()));
        resultOrder.setTotalQuantity(cart.getTotalQuantity());
    }

    private CartItem getCartItem(CartItem cartItem) {
        try {
            return (CartItem) cartItem.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAvailableInStock(@NotNull Cart cart) {
        for (CartItem cartItem : cart.getItems()) {
            try {
                verifyStock(cartItem);
            } catch (ProductNotFoundException | OutOfStockException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsOrder(String id) {
        lock.readLock().lock();
        try {
            orderDao.getOrder(id);
        } catch (IllegalArgumentException | OrderNotFoundException e) {
            return false;
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }

    private void verifyStock(CartItem cartItem) throws OutOfStockException {
        Product product = productDao.getProduct(cartItem.getProduct().getId());
        if (product.getStock() < cartItem.getQuantity()) {
            throw new OutOfStockException();
        }
    }


    @Override
    public void placeOrder(Order order) throws InvalidOrderException {
        lock.writeLock().lock();
        try {
            if (order != null && isAvailableInStock(order)) {
                reservedInstances(order);
            } else {
                throw new InvalidOrderException();
            }
            saveInDao(order);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void reservedInstances(Cart cart) {
        cart.getItems()
                .forEach(cartItem -> {
                    Product product = productDao.getProduct(cartItem.getProduct().getId());
                    synchronized (product) {
                        product.setStock(product.getStock() - cartItem.getQuantity());
                    }
                });
    }


    private void saveInDao(Order order) {
        if (order.getSecureId() != null) {
            orderDao.save(order);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<PaymentType> getPaymentTypes() {
        return Arrays.asList(PaymentType.values());
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    private BigDecimal calculateDeliveryPrice() {
        return new BigDecimal(DELIVERY_PRICE);
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }
}
