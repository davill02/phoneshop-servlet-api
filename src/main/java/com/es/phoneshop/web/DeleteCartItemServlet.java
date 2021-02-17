package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.DefaultCartService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.es.phoneshop.web.ServletsConstants.CART_PATH;
import static com.es.phoneshop.web.ServletsConstants.PRODUCTS_PATH;

public class DeleteCartItemServlet extends HttpServlet {

    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cart cart = cartService.getCart(request);
        try {
            Long productId = Long.parseLong(request.getPathInfo().substring(1));
            cartService.delete(cart, productId);
        } catch (NumberFormatException ignored) {

        }
        response.sendRedirect(getServletContext().getContextPath() + PRODUCTS_PATH + CART_PATH);
    }
}
