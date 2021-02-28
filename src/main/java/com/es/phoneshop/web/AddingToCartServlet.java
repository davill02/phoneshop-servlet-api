package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.DefaultCartService;
import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static com.es.phoneshop.web.ServletsConstants.ATTR_ERROR;
import static com.es.phoneshop.web.ServletsConstants.MINI_CART_JSP;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUANTITY;
import static com.es.phoneshop.web.ServletsExceptionMessages.CANT_PARSE_VALUE;
import static com.es.phoneshop.web.ServletsExceptionMessages.NEED_INTEGER;
import static com.es.phoneshop.web.ServletsExceptionMessages.NOT_A_NUMBER;
import static com.es.phoneshop.web.ServletsExceptionMessages.TOO_BIG_NUMBER;

public abstract class AddingToCartServlet extends HttpServlet {
    private CartService cartService;
    private Map<String, String> exceptionMap;

    public AddingToCartServlet() {
        cartService = DefaultCartService.getInstance();
        createExceptionMap();
    }

    private void createExceptionMap() {
        exceptionMap = new HashMap<>();
        exceptionMap.put(NumberFormatException.class.getName(), NOT_A_NUMBER);
        exceptionMap.put(ClassCastException.class.getName(), NEED_INTEGER);
        exceptionMap.put(ParseException.class.getName(), CANT_PARSE_VALUE);
        exceptionMap.put(ArithmeticException.class.getName(), TOO_BIG_NUMBER);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer quantity = getQuantity(request, response);
        if (quantity == null) {
            return;
        }
        Cart cart = (Cart) request.getSession().getAttribute(ATTR_CART);
        if (addToCart(request, response, quantity, cart)) {
            return;
        }
        sendRedirect(request, response);
    }

    protected abstract void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException;

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

    protected abstract Long parseId(HttpServletRequest request);

    @Nullable
    private Integer getQuantity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer quantity;
        try {
            quantity = getQuantity(request);
        } catch (NumberFormatException | ClassCastException | ParseException | ArithmeticException e) {
            String exceptionMessage = exceptionMap.get(e.getClass().getName());
            request.setAttribute(ATTR_ERROR, exceptionMessage);
            request.getRequestDispatcher(MINI_CART_JSP).include(request, response);
            doGet(request, response);
            return null;
        }
        return quantity;
    }

    private Integer getQuantity(HttpServletRequest request) throws ParseException, ClassCastException, ArithmeticException {
        String quantityString = request.getParameter(PARAM_QUANTITY);
        Locale locale = request.getLocale();
        return ServletUtils.parseQuantity(locale, quantityString);
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
