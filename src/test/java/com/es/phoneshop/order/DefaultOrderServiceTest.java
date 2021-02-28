package com.es.phoneshop.order;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import jdk.dynalink.linker.LinkerServices;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderServiceTest {

    private static final int QUANTITY = 12;
    @Mock
    private OrderDao orderDao;
    private OrderService orderService = DefaultOrderService.getInstance();

    @Before
    public void setUp() {
        DefaultOrderService.setOrderDao(orderDao);
    }


    @Test
    public void shouldCreateOrder() {
        Cart defaultCart = createOneItemCart();

        Order result = orderService.getOrder(defaultCart);

        assertNotNull(result.getTotalPrice());
        assertEquals(defaultCart.getItems().size(), result.getItems().size());
    }

    private Cart createOneItemCart() {
        Cart cart = new Cart();
        CartItem cartItem = new CartItem(new Product(), QUANTITY);
        cart.getItems().add(cartItem);
        return cart;
    }

    @Test
    public void shouldPlaceOrder() {
        Order order = new Order();
        order.setSecureId(UUID.randomUUID().toString());

        orderService.placeOrder(order);

        verify(orderDao).save(eq(order));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIllegalArgument() {
        Order order = new Order();

        orderService.placeOrder(order);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldDoNothingAndThrowException() {
        orderService.placeOrder(null);
    }

    @Test
    public void shouldReturnEmptyOrder(){
        Order result = orderService.getOrder(null);

        assertNull(result.getTotalPrice());
        assertNull(result.getDeliveryPrice());
    }

    @Test
    public void shouldReturnPaymentTypes(){
        List<PaymentType> paymentTypeList = orderService.getPaymentTypes();
        boolean isContainsCash = paymentTypeList.contains(PaymentType.cash);
        boolean isContainsCard = paymentTypeList.contains(PaymentType.card);


        assertTrue(isContainsCard);
        assertTrue(isContainsCash);
    }
}