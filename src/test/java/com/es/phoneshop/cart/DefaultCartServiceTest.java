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
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private static final String ATTR_CART = "cart";
    public static final int INVALID_QUANTITY = -6;

    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;
    @Mock
    private ProductDao productDao;

    private final Currency usd = Currency.getInstance(CURRENCY_CODE);
    private DefaultCartService cartService;
    private final Product notDefaultProduct = new Product(CODE, DESCRIPTION, PRICE, usd, STOCK, IMAGE_URL);
    private final Cart cart = new Cart();

    @Before
    public void setUp() {
        cartService = (DefaultCartService) DefaultCartService.getInstance();
        notDefaultProduct.setId(ID);
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
    public void shouldCreateCart(){
        when(session.getAttribute(ATTR_CART)).thenReturn(null);

        cartService.getCart(request);

        verify(session,times(1)).setAttribute(eq(ATTR_CART),any(Cart.class));
    }

    @Test(expected = InvalidQuantityException.class)
    public void shouldThrowInvalidQuantityException() throws InvalidQuantityException, OutOfStockException {
        cartService.add(cart,ID, INVALID_QUANTITY);
    }

}