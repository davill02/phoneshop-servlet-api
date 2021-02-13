package com.es.phoneshop.recentlyviewed;

import com.es.phoneshop.model.product.Product;
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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRecentlyViewedServiceTest {
    private static final String CODE = "iphone7";
    private static final String DESCRIPTION = "IPhone 7";
    private static final BigDecimal PRICE = new BigDecimal(1200);
    private static final int STOCK = 9;
    private static final String IMAGE_URL = "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg";
    private static final String CURRENCY_CODE = "USD";

    @Mock
    HttpServletRequest request;
    @Mock
    HttpSession session;

    private final DefaultRecentlyViewedService service = (DefaultRecentlyViewedService) DefaultRecentlyViewedService.getInstance();
    private final Currency usd = Currency.getInstance(CURRENCY_CODE);
    private final Product notDefaultProduct = new Product(CODE, DESCRIPTION, PRICE, usd, STOCK, IMAGE_URL);
    private RecentlyViewed products;

    @Before
    public void setUp() {
        when(request.getSession()).thenReturn(session);
        products = new RecentlyViewed();
    }

    @After
    public void tearDown() {
        products.getRecentlyViewed().clear();
    }

    @Test
    public void shouldGetNewRecently() {
        RecentlyViewed result = service.getRecentlyViewed(request);
        List<Product> resultProducts = result.getRecentlyViewed();

        assertTrue(resultProducts.isEmpty());
    }

    @Test
    public void shouldAddTwoProducts() {
        Product anotherProduct = new Product(2L, "sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg");

        service.add(products, notDefaultProduct);
        service.add(products, anotherProduct);
        int result = products.getRecentlyViewed().size();

        assertEquals(2, result);
    }

    @Test
    public void shouldAddManyProductAndLeaveFour() {
        for (int i = 0; i < 20; i++) {
            service.add(products, new Product());
        }
        int result = products.getRecentlyViewed().size();

        assertEquals(DefaultRecentlyViewedService.MAX_COUNT_RECENTLY_VIEWED,result);
    }

    @Test
    public void shouldAddThreeProductsAndLeaveTwo() {
        Product anotherProduct = new Product(2L, "sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg");

        service.add(products, notDefaultProduct);
        service.add(products, anotherProduct);
        service.add(products, notDefaultProduct);
        int result = products.getRecentlyViewed().size();
        Product firstProduct = products.getRecentlyViewed().get(0);

        assertEquals(2, result);
        assertEquals(notDefaultProduct, firstProduct);
    }

    @Test
    public void shouldAddNewProduct() {
        service.add(products, notDefaultProduct);
        List<Product> resultProducts = products.getRecentlyViewed();

        assertEquals(notDefaultProduct, resultProducts.get(0));
    }
}
