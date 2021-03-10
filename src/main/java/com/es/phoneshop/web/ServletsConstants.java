package com.es.phoneshop.web;

import java.util.Currency;

public final class ServletsConstants {
    public static final String PRODUCTS = "products";
    public static final Currency USD = Currency.getInstance("USD");

    public static final String DETAILS_PAGE_PATH = "/WEB-INF/pages/productDetails.jsp";
    public static final String LIST_PAGE_PATH = "/WEB-INF/pages/productList.jsp";
    public static final String CART_PAGE_PATH = "/WEB-INF/pages/cart.jsp";
    public static final String CHECKOUT_PAGE_PATH = "/WEB-INF/pages/checkout.jsp";
    public static final String OVERVIEW_PAGE_PATH = "/WEB-INF/pages/overview.jsp";
    public static final String MINI_CART_JSP = "/WEB-INF/pages/minicart.jsp";
    public static final String PRODUCTS_PATH = "/products";
    public static final String CART_PATH = "/cart";
    public static final String ORDER_PATH = "/order";
    public static final String OVERVIEW_PATH = "/overview";
    public static final String ADVANCED_SEARCH_PAGE_PATH = "/WEB-INF/pages/search.jsp";

    public static final String PARAM_QUERY = "query";
    public static final String PARAM_ORDER = "order";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_QUANTITY = "quantity";
    public static final String PARAM_ERROR = "error";
    public static final String PARAM_ID = "id";
    public static final String PARAM_START_WITH_DEFAULT_PRODUCTS = "startWithDefaultProducts";
    public static final String PARAM_FIRSTNAME = "firstname";
    public static final String PARAM_LASTNAME = "lastname";
    public static final String PARAM_ADDRESS = "address";
    public static final String PARAM_PAYMENT_TYPE = "paymentType";
    public static final String PARAM_DATE = "date";
    public static final String PARAM_PHONE = "phone";
    public static final String PARAM_MIN_PRICE = "minPrice";
    public static final String PARAM_MAX_PRICE = "maxPrice";
    public static final String PARAM_SEARCH_TYPE = "searchType";

    public static final String PARAM_ERROR_VALUE_OUT_OF_STOCK = "OutOfStock";
    public static final String PARAM_ERROR_VALUE_NO_ERROR = "noError";

    public static final String ATTR_RECENTLY_VIEWED = "recentlyViewed";
    public static final String ATTR_PRODUCT = "product";
    public static final String ATTR_CART = "cart";
    public static final String ATTR_ERROR = "error";
    public static final String ATTR_EXCEPTION_MAP = "exceptionMap";
    public static final String ATTR_ORDER = "order";
    public static final String ATTR_PAYMENT_TYPES = "paymentTypes";


    private ServletsConstants() {
    }
}
