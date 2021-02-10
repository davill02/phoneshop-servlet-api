package com.es.phoneshop.web;

import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ProductDetailsPageServlet extends HttpServlet {
    public static final String ATTR_PRODUCT = "product";
    public static final String PAGE_PATH = "/WEB-INF/pages/productDetails.jsp";
    public static final String PARAM_QUANTITY = "quantity";
    public static final String ATTR_CART = "cart";
    private ProductDao productDao;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ATTR_CART,cartService.getCart());
        request.setAttribute(ATTR_PRODUCT, productDao.getProduct(parseId(request)));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String quantity = request.getParameter(PARAM_QUANTITY);
        cartService.add(parseId(request), Optional.ofNullable(Integer.parseInt(quantity)).orElse(0));
        request.setAttribute(ATTR_PRODUCT, productDao.getProduct(parseId(request)));
        request.setAttribute(ATTR_CART,cartService.getCart());
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }

    private Long parseId(HttpServletRequest request) {
        String id = request.getPathInfo().substring(1);
        return Long.parseLong(id);
    }
}
