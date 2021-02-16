package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.DefaultCartService;
import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.recentlyviewed.DefaultRecentlyViewedService;
import com.es.phoneshop.recentlyviewed.RecentlyViewed;
import com.es.phoneshop.recentlyviewed.RecentlyViewedService;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static com.es.phoneshop.web.ServletsConstants.ATTR_ERROR;
import static com.es.phoneshop.web.ServletsConstants.ATTR_PRODUCT;
import static com.es.phoneshop.web.ServletsConstants.ATTR_RECENTLY_VIEWED;
import static com.es.phoneshop.web.ServletsConstants.DETAILS_PAGE_PATH;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ERROR;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUANTITY;
import static com.es.phoneshop.web.ServletsConstants.PRODUCTS_PATH;
import static com.es.phoneshop.web.ServletsExceptionMessages.CANT_PARSE_VALUE;
import static com.es.phoneshop.web.ServletsExceptionMessages.NEED_INTEGER;
import static com.es.phoneshop.web.ServletsExceptionMessages.NOT_A_NUMBER;
import static com.es.phoneshop.web.ServletsExceptionMessages.SUCCESS_MSG;
import static com.es.phoneshop.web.ServletsExceptionMessages.TOO_BIG_NUMBER;

public class ProductDetailsPageServlet extends HttpServlet {
    private ProductDao productDao;
    private CartService cartService;
    private NumberFormat numberFormat;
    private Map<String, String> exceptionMap;
    private RecentlyViewedService recentlyViewedService;

    public RecentlyViewedService getRecentlyViewedService() {
        return recentlyViewedService;
    }

    public void setRecentlyViewedService(RecentlyViewedService recentlyViewedService) {
        this.recentlyViewedService = recentlyViewedService;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        recentlyViewedService = DefaultRecentlyViewedService.getInstance();
        createExceptionMap();
    }

    private void createExceptionMap() {
        exceptionMap = new HashMap<>();
        exceptionMap.put(NumberFormatException.class.getName(), NOT_A_NUMBER);
        exceptionMap.put(ClassCastException.class.getName(), NEED_INTEGER);
        exceptionMap.put(ParseException.class.getName(), CANT_PARSE_VALUE);
        exceptionMap.put(ArithmeticException.class.getName(), TOO_BIG_NUMBER);
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ATTR_CART, cartService.getCart(request));
        request.setAttribute(ATTR_RECENTLY_VIEWED, recentlyViewedService.getRecentlyViewed(request));
        request.setAttribute(ATTR_PRODUCT, productDao.getProduct(parseId(request)));
        request.getRequestDispatcher(DETAILS_PAGE_PATH).forward(request, response);
        RecentlyViewed recentlyViewed = (RecentlyViewed) request.getSession().getAttribute(ATTR_RECENTLY_VIEWED);
        recentlyViewedService.add(recentlyViewed, productDao.getProduct(parseId(request)));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer quantity;
        quantity = getQuantity(request, response);
        if (quantity == null) {
            return;
        }
        Cart cart = (Cart) request.getSession().getAttribute(ATTR_CART);
        if (addToCart(request, response, quantity, cart)) {
            return;
        }
        response.sendRedirect(getServletContext().getContextPath() + PRODUCTS_PATH
                + request.getPathInfo() + "?" + PARAM_ERROR + "=" + SUCCESS_MSG);
    }

    private boolean addToCart(HttpServletRequest request, HttpServletResponse response, Integer quantity, Cart cart) throws ServletException, IOException {
        try {
            cartService.add(cart, parseId(request), Optional.ofNullable(quantity).orElse(0));
        } catch (OutOfStockException | InvalidQuantityException e) {
            request.setAttribute(ATTR_ERROR, e.getMessage());
            doGet(request, response);
            return true;
        }
        return false;
    }

    @Nullable
    private Integer getQuantity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer quantity;
        try {
            quantity = getQuantity(request);
        } catch (NumberFormatException | ClassCastException | ParseException | ArithmeticException e) {
            String exceptionMessage = exceptionMap.get(e.getClass().getName());
            request.setAttribute(ATTR_ERROR, exceptionMessage);
            doGet(request, response);
            return null;
        }
        return quantity;
    }

    private Integer getQuantity(HttpServletRequest request) throws ParseException, ClassCastException, ArithmeticException {
        String quantityString = request.getParameter(PARAM_QUANTITY);
        Locale locale = request.getLocale();
        return ServletUtils.parseQuantity(locale,quantityString);
    }

    private Long parseId(HttpServletRequest request) {
        String id = request.getPathInfo().substring(1);
        return Long.parseLong(id);
    }
}
