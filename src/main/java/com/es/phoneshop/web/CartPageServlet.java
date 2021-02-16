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
import java.util.Locale;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static com.es.phoneshop.web.ServletsConstants.CART_PAGE_PATH;
import static com.es.phoneshop.web.ServletsConstants.CART_PATH;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ID;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUANTITY;
import static com.es.phoneshop.web.ServletsConstants.PRODUCTS_PATH;

public class CartPageServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ATTR_CART, cartService.getCart(request));
        request.getRequestDispatcher(CART_PAGE_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] quantities = request.getParameterValues(PARAM_QUANTITY);
        String[] productIds = request.getParameterValues(PARAM_ID);
        Cart cart = cartService.getCart(request);
        for (int i = 0; i < quantities.length; i++) {
            updateCartAndHandleException(request.getLocale(), quantities[i], productIds[i], cart);
        }
        response.sendRedirect(getServletContext().getContextPath() + PRODUCTS_PATH + CART_PATH);
    }

    private void updateCartAndHandleException(Locale locale, String quantityString, String productId, Cart cart) {
        try {
            int quantity = ServletUtils.parseQuantity(locale, quantityString);
            cartService.update(cart, Long.parseLong(productId), quantity);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
        } catch (OutOfStockException e) {
            e.printStackTrace();
        }
    }
}
