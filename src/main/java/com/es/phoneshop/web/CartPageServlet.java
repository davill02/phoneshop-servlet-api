package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.DefaultCartService;
import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static com.es.phoneshop.web.ServletsConstants.ATTR_EXCEPTION_MAP;
import static com.es.phoneshop.web.ServletsConstants.CART_PAGE_PATH;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ID;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUANTITY;
import static com.es.phoneshop.web.ServletsExceptionMessages.CANT_PARSE_VALUE;
import static com.es.phoneshop.web.ServletsExceptionMessages.NEED_INTEGER;

public class CartPageServlet extends HttpServlet {
    private CartService cartService;
    private Map<Long, List<String>> exceptionMap = new HashMap<>();
    private Locale locale;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ATTR_EXCEPTION_MAP, exceptionMap);
        request.setAttribute(ATTR_CART, cartService.getCart(request));
        request.getRequestDispatcher(CART_PAGE_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String[] quantities = request.getParameterValues(PARAM_QUANTITY);
        String[] productIds = request.getParameterValues(PARAM_ID);
        exceptionMap = new HashMap<>();
        Cart cart = cartService.getCart(request);
        locale = request.getLocale();
        for (int i = 0; i < quantities.length; i++) {
            updateCartAndHandleException(quantities[i], productIds[i], cart);
        }
        doGet(request, response);
    }

    private void updateCartAndHandleException(String quantityString, String productId, Cart cart) {
        try {
            int quantity = ServletUtils.parseQuantity(locale, quantityString);
            cartService.update(cart, Long.parseLong(productId), quantity);
        } catch (ParseException e) {
            exceptionMap.put(Long.parseLong(productId), returnMessageAndQueryList(CANT_PARSE_VALUE, quantityString));
        } catch (InvalidQuantityException | OutOfStockException e) {
            exceptionMap.put(Long.parseLong(productId), returnMessageAndQueryList(e.getMessage(), quantityString));
        } catch (ClassCastException e) {
            exceptionMap.put(Long.parseLong(productId), returnMessageAndQueryList(NEED_INTEGER, quantityString));
        }
    }

    private List<String> returnMessageAndQueryList(String message, String query) {
        List<String> messages = new ArrayList<>();
        messages.add(message);
        messages.add(query);
        return messages;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
