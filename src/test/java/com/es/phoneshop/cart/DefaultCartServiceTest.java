package com.es.phoneshop.cart;

import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest {
    private static final int MORE_THAN_STOCK = 10;
    private static final String CODE = "iphone7";
    private static final String DESCRIPTION = "IPhone 7";
    private static final BigDecimal PRICE = new BigDecimal(1200);
    private static final int STOCK = 9;
    private static final String IMAGE_URL = "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg";
    private static final String CURRENCY_CODE = "USD";
    private static final long ID = 1L;
    private static final int LESS_THAN_STOCK = 6;
    private static final int ONE_COUNT = 1;
    private static final int ONE_MORE = 1;
    public static final int INVALID_QUANTITY = -6;
    public static final int QUANTITY = 10;
    private static final Long NON_EXIST_ID = 3232L;
    public static final long ID1 = 1L;
    public static final long ID2 = 2L;
    public static final int EXPECTED_SIZE = 1;

    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;
    @Mock
    private ProductDao productDao;

    private final Currency usd = Currency.getInstance(CURRENCY_CODE);
    private DefaultCartService cartService;
    private final Product notDefaultProduct = new Product(CODE, DESCRIPTION, PRICE, usd, STOCK, IMAGE_URL);
    private Cart cart;
    private final BigDecimal standartSum = new BigDecimal(10);
    Product product1 = new Product(ID1, new BigDecimal(100), STOCK);

    @Before
    public void setUp() {
        cartService = (DefaultCartService) DefaultCartService.getInstance();
        notDefaultProduct.setId(ID);
        cart = new Cart();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(eq(ATTR_CART))).thenReturn(cart);
        when(productDao.getProduct(any())).thenReturn(notDefaultProduct);
        cartService.setProductDao(productDao);
    }

    @After
    public void tearDown() {
        cart.getItems().clear();
    }

    @Test
    public void shouldAddToCart() throws OutOfStockException, InvalidQuantityException {
        cartService.add(cart, ID, LESS_THAN_STOCK);

        int result = cartService.getCart(request).getItems().size();
        Product resultProduct = cartService.getCart(request).getItems().get(0).getProduct();
        int resultQuantity = cartService.getCart(request).getItems().get(0).getQuantity();

        assertEquals(ONE_COUNT, result);
        assertEquals(notDefaultProduct, resultProduct);
        assertEquals(LESS_THAN_STOCK, resultQuantity);
    }

    @Test(expected = OutOfStockException.class)
    public void shouldThrowException() throws OutOfStockException, InvalidQuantityException {
        cartService.add(cart, ID, MORE_THAN_STOCK);
    }

    @Test
    public void shouldSumQuantity() throws OutOfStockException, InvalidQuantityException {
        cartService.add(cart, ID, LESS_THAN_STOCK);
        cartService.add(cart, ID, ONE_MORE);

        CartItem result = cartService.getCart(request).getItems().get(0);

        assertEquals(LESS_THAN_STOCK + ONE_MORE, result.getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void shouldThrowExceptionAndNotChangeCartItem() throws OutOfStockException, InvalidQuantityException {
        try {
            cartService.add(cart, ID, LESS_THAN_STOCK);
            cartService.add(cart, ID, MORE_THAN_STOCK);
        } finally {
            CartItem result = cartService.getCart(request).getItems().get(0);

            assertEquals(LESS_THAN_STOCK, result.getQuantity());
        }
    }

    @Test
    public void shouldCreateCart() {
        when(session.getAttribute(ATTR_CART)).thenReturn(null);

        cartService.getCart(request);

        verify(session, times(1)).setAttribute(eq(ATTR_CART), any(Cart.class));
    }

    @Test(expected = InvalidQuantityException.class)
    public void shouldThrowInvalidQuantityException() throws InvalidQuantityException, OutOfStockException {
        cartService.add(cart, ID, INVALID_QUANTITY);
    }

    @Test
    public void shouldCreateCartIfRequestNull() {
        Cart result = cartService.getCart(null);

        assertNotNull(result);
    }

    @Test
    public void shouldUpdateCart() throws InvalidQuantityException, OutOfStockException {
        cart.getItems().add(new CartItem(notDefaultProduct, ONE_COUNT));

        cartService.update(cart, ID, LESS_THAN_STOCK);
        CartItem result = cartService.getCart(request).getItems().get(0);

        assertEquals(LESS_THAN_STOCK, result.getQuantity());
    }

    @Test(expected = OutOfStockException.class)
    public void shouldUpdateAndThrowOutOfStock() throws InvalidQuantityException, OutOfStockException {
        cart.getItems().add(new CartItem(notDefaultProduct, ONE_COUNT));

        try {
            cartService.update(cart, ID, MORE_THAN_STOCK);
        } finally {
            CartItem result = cartService.getCart(request).getItems().get(0);

            assertEquals(ONE_COUNT, result.getQuantity());
        }
    }

    @Test(expected = InvalidQuantityException.class)
    public void shouldUpdateAndThrowInvalidQuantity() throws InvalidQuantityException, OutOfStockException {
        cart.getItems().add(new CartItem(notDefaultProduct, ONE_COUNT));

        try {
            cartService.update(cart, ID, INVALID_QUANTITY);
        } finally {
            CartItem result = cartService.getCart(request).getItems().get(0);

            assertEquals(ONE_COUNT, result.getQuantity());
        }
    }

    @Test
    public void shouldUpdateAndRecalculatePrice() throws InvalidQuantityException, OutOfStockException {
        cart.getItems().add(new CartItem(notDefaultProduct, ONE_COUNT));
        BigDecimal expected = notDefaultProduct.getPrice().multiply(new BigDecimal(LESS_THAN_STOCK));

        cartService.update(cart, ID, LESS_THAN_STOCK);
        BigDecimal result = cart.getTotalPrice();

        assertEquals(expected, result);
    }

    @Test
    public void shouldUpdateAndRecalculateQuantity() throws InvalidQuantityException, OutOfStockException {
        fillCart();
        int expected = QUANTITY - ONE_MORE + LESS_THAN_STOCK;

        cartService.update(cart, ID, LESS_THAN_STOCK);
        int result = cart.getTotalQuantity();

        assertEquals(expected, result);

    }

    @Test
    public void shouldUpdateAndDontRecalculate() throws InvalidQuantityException, OutOfStockException {
        fillCart();

        cartService.update(cart, NON_EXIST_ID, MORE_THAN_STOCK);
        BigDecimal resultPrice = cart.getTotalPrice();
        int resultQuantity = cart.getTotalQuantity();

        assertEquals(QUANTITY, resultQuantity);
        assertEquals(standartSum, resultPrice);
    }

    private void fillCart() {
        BigDecimal j = new BigDecimal(1);

        for (long i = 0; i < QUANTITY; i++) {
            cart.getItems().add(new CartItem(new Product(i, j, MORE_THAN_STOCK), ONE_COUNT));
        }
    }

    @Test
    public void shouldDeleteItem() {
        cart.getItems().add(new CartItem(notDefaultProduct, ONE_COUNT));

        cartService.delete(cart, ID);
        List<CartItem> result = cart.getItems();

        assertTrue(result.isEmpty());

    }

    @Test
    public void shouldDeleteNonExistItem() {
        fillCart();

        cartService.delete(cart, NON_EXIST_ID);
        int result = cart.getItems().size();

        assertEquals(QUANTITY, result);
    }

    @Test
    public void shouldDoNothing() {
        cartService.normalizeCart(null);

        verify(productDao, never()).getProduct(any());
    }

    @Test
    public void shouldNormalizeCartButDoNothing() {
        Cart cart = getValidCart();


        cartService.normalizeCart(cart);

        verify(productDao, times(2)).getProduct(anyLong());
    }

    private Cart getValidCart() {
        Cart cart = new Cart();
        List<CartItem> cartItems = new ArrayList<>();
        Product product2 = new Product(ID2, new BigDecimal(200), STOCK);
        when(productDao.getProduct(anyLong())).thenReturn(product1, product2);
        cartItems.add(new CartItem(product1, 5));
        cartItems.add(new CartItem(product2, 7));
        cart.setItems(cartItems);
        return cart;
    }

    @Test
    public void shouldNormalizeCart() {
        Cart cart = getValidCart();
        cart.getItems().get(0).setQuantity(MORE_THAN_STOCK);

        cartService.normalizeCart(cart);
        int result = cart.getItems().get(0).getQuantity();

        verify(productDao, times(2)).getProduct(anyLong());
        assertEquals(STOCK, result);
    }

    @Test
    public void shouldNormalizeCartAndDeleteItem() {
        Cart cart = getValidCart();
        product1.setStock(0);

        cartService.normalizeCart(cart);
        int result = cart.getItems().size();

        assertEquals(EXPECTED_SIZE, result);
    }
}

