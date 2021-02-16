package com.es.phoneshop.web;

import java.util.Currency;

public final class ServletsConstants {
    public static final String PRODUCTS = "products";
    public static final Currency USD = Currency.getInstance("USD");

    public static final String DETAILS_PAGE_PATH = "/WEB-INF/pages/productDetails.jsp";
    public static final String LIST_PAGE_PATH = "/WEB-INF/pages/productList.jsp";
    public static final String CART_PAGE_PATH = "/WEB-INF/pages/cart.jsp";
    public static final String PRODUCTS_PATH = "/products";
    public static final String CART_PATH = "/cart";

    public static final String PARAM_QUERY = "query";
    public static final String PARAM_ORDER = "order";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_QUANTITY = "quantity";
    public static final String PARAM_ERROR = "error";
    public static final String PARAM_ID = "id";
    public static final String PARAM_START_WITH_DEFAULT_PRODUCTS = "startWithDefaultProducts";

    public static final String ATTR_RECENTLY_VIEWED = "recentlyViewed";
    public static final String ATTR_PRODUCT = "product";
    public static final String ATTR_CART = "cart";
    public static final String ATTR_ERROR = "error";


    private ServletsConstants() {
    }
}
