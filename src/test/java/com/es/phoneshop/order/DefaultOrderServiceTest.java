package com.es.phoneshop.order;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.order.exceptions.InvalidOrderException;
import com.es.phoneshop.order.exceptions.OrderNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderServiceTest {

    private static final int QUANTITY = 12;
    private static final String ID = "ID";
    private static final int EXPECTED_STOCK = 0;

    @Mock
    private OrderDao orderDao;
    @Mock
    private ProductDao productDao;

    private final OrderService orderService = DefaultOrderService.getInstance();
    public Product product = new Product();

    @Before
    public void setUp() {
        ((DefaultOrderService) orderService).setOrderDao(orderDao);
        ((DefaultOrderService) orderService).setProductDao(productDao);
        product.setStock(QUANTITY);
        when(productDao.getProduct(any())).thenReturn(product);
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
        CartItem cartItem = new CartItem(product, QUANTITY);
        cart.getItems().add(cartItem);
        return cart;
    }

    @Test
    public void shouldPlaceOrder() throws InvalidOrderException {
        Order order = new Order();
        order.setSecureId(UUID.randomUUID().toString());

        orderService.placeOrder(order);

        verify(orderDao).save(eq(order));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIllegalArgument() throws InvalidOrderException {
        Order order = new Order();

        orderService.placeOrder(order);
    }

    @Test(expected = InvalidOrderException.class)
    public void shouldDoNothingAndThrowException() throws InvalidOrderException {
        orderService.placeOrder(null);
    }

    @Test
    public void shouldReturnEmptyOrder() {
        Order result = orderService.getOrder(null);

        assertNull(result.getTotalPrice());
        assertNull(result.getDeliveryPrice());
    }

    @Test
    public void shouldReturnPaymentTypes() {
        List<PaymentType> paymentTypeList = orderService.getPaymentTypes();
        boolean isContainsCash = paymentTypeList.contains(PaymentType.CASH);
        boolean isContainsCard = paymentTypeList.contains(PaymentType.CARD);


        assertTrue(isContainsCard);
        assertTrue(isContainsCash);
    }

    @Test
    public void shouldContainOrder() {
        boolean result = orderService.containsOrder(ID);

        assertTrue(result);
    }

    @Test
    public void shouldNotContainOrderByOrderNotFound() {
        when(orderDao.getOrder(eq(ID))).thenThrow(OrderNotFoundException.class);

        boolean result = orderService.containsOrder(ID);

        assertFalse(result);
    }

    @Test
    public void shouldNotContainOrderByIllegalArgument() {
        when(orderDao.getOrder(eq(ID))).thenThrow(IllegalArgumentException.class);

        boolean result = orderService.containsOrder(ID);

        assertFalse(result);
    }

    @Test
    public void shouldAvailableInStock() {
        Cart cart = createOneItemCart();

        boolean result = orderService.isAvailableInStock(cart);

        assertTrue(result);
    }

    @Test
    public void shouldNotAvailableInStock() {
        Cart cart = createOneItemCart();
        cart.getItems().get(0).setQuantity(QUANTITY + 1);

        boolean result = orderService.isAvailableInStock(cart);

        assertFalse(result);
    }

    @Test
    public void shouldPlaceOrderAndChangeProductStock() throws InvalidOrderException {
        Cart cart = createOneItemCart();
        Order order = new Order();
        order.setSecureId(ID);
        order.setItems(cart.getItems());

        orderService.placeOrder(order);
        int resultStock = product.getStock();

        assertEquals(EXPECTED_STOCK, resultStock);
    }
}