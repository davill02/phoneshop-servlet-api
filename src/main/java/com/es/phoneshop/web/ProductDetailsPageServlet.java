package com.es.phoneshop.web;

import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.recentlyviewed.DefaultRecentlyViewedService;
import com.es.phoneshop.recentlyviewed.RecentlyViewed;
import com.es.phoneshop.recentlyviewed.RecentlyViewedService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;

import static com.es.phoneshop.web.ServletsConstants.ATTR_CART;
import static com.es.phoneshop.web.ServletsConstants.ATTR_PRODUCT;
import static com.es.phoneshop.web.ServletsConstants.ATTR_RECENTLY_VIEWED;
import static com.es.phoneshop.web.ServletsConstants.DETAILS_PAGE_PATH;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ERROR;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ERROR_VALUE_NO_ERROR;
import static com.es.phoneshop.web.ServletsConstants.PRODUCTS_PATH;


public class ProductDetailsPageServlet extends AddingToCartServlet {
    private ProductDao productDao;
    private NumberFormat numberFormat;
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
        recentlyViewedService = DefaultRecentlyViewedService.getInstance();
    }

    @Override
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(getServletContext().getContextPath() + PRODUCTS_PATH
                + request.getPathInfo() + "?" + PARAM_ERROR + "=" + PARAM_ERROR_VALUE_NO_ERROR);
    }

    @Override
    protected Long parseId(HttpServletRequest request) {
        String id = request.getPathInfo().substring(1);
        return Long.parseLong(id);
    }

    public void setCartService(CartService cartService) {
        super.setCartService(cartService);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute(ATTR_CART, super.getCartService().getCart(request));
        request.setAttribute(ATTR_RECENTLY_VIEWED, recentlyViewedService.getRecentlyViewed(request));
        request.setAttribute(ATTR_PRODUCT, productDao.getProduct(parseId(request)));
        request.getRequestDispatcher(DETAILS_PAGE_PATH).forward(request, response);
        RecentlyViewed recentlyViewed = (RecentlyViewed) request.getSession().getAttribute(ATTR_RECENTLY_VIEWED);
        recentlyViewedService.add(recentlyViewed, productDao.getProduct(parseId(request)));
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

}
