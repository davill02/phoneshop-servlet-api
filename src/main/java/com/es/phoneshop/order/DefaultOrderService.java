package com.es.phoneshop.order;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartItem;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {
    public static final String DELIVERY_PRICE = "5";
    private static OrderService service;
    private static OrderDao orderDao;
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private DefaultOrderService() {
        orderDao = ArrayListOrderDao.getInstance();
    }

    public static OrderService getInstance() {
        if (service == null) {
            service = new DefaultOrderService();
        }
        return service;
    }


    @Override
    public Order getOrder(Cart cart) {
        lock.writeLock().lock();
        try {

            Order resultOrder = new Order();
            if (cart != null) {
                resultOrder.setItems(cart.getItems().stream().map(cartItem -> {
                    try {
                        return (CartItem) cartItem.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toUnmodifiableList()));
                resultOrder.setDeliveryPrice(calculateDeliveryPrice());
                resultOrder.setSubPrice(cart.getTotalPrice());
                resultOrder.setTotalPrice(calculateDeliveryPrice().add(cart.getTotalPrice()));
                resultOrder.setTotalQuantity(cart.getTotalQuantity());
            }
            return resultOrder;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void placeOrder(Order order) {
        lock.writeLock().lock();
        try {
            saveInDao(order);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void saveInDao(Order order) {
        if (order != null && order.getSecureId() != null) {
            orderDao.save(order);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<PaymentType> getPaymentTypes() {
        return Arrays.asList(PaymentType.values());
    }

    private BigDecimal calculateDeliveryPrice() {
        return new BigDecimal(DELIVERY_PRICE);
    }

    public static void setOrderDao(OrderDao orderDao) {
        DefaultOrderService.orderDao = orderDao;
    }
}
