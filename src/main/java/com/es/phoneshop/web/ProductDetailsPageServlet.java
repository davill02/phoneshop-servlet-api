package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.DefaultCartService;
import com.es.phoneshop.cart.exceptions.InvalidQuantityException;
import com.es.phoneshop.cart.exceptions.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;

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

public class ProductDetailsPageServlet extends HttpServlet {
    public static final String ATTR_PRODUCT = "product";
    public static final String PAGE_PATH = "/WEB-INF/pages/productDetails.jsp";
    public static final String PARAM_QUANTITY = "quantity";
    public static final String ATTR_CART = "cart";
    public static final String ATTR_ERROR = "error";
    public static final String NOT_A_NUMBER = "Not a number";
    public static final String NOT_A_NUMBER_MSG = NOT_A_NUMBER;
    public static final String PRODUCTS_PATH = "/products";
    public static final String SUCCESS_MSG = "noError";
    public static final String PARAM_ERROR = "error";
    public static final String NEED_INTEGER = "Need integer";
    public static final String CANT_PARSE_VALUE = "Cant parse value";
    public static final String TOO_BIG_NUMBER = "Too big number";

    private ProductDao productDao;
    private CartService cartService;
    private NumberFormat numberFormat;
    private Map<String,String> exceptionMap;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
        exceptionMap = new HashMap<>();
        exceptionMap.put(NumberFormatException.class.getName(),NOT_A_NUMBER);
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
        request.setAttribute(ATTR_PRODUCT, productDao.getProduct(parseId(request)));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute(ATTR_PRODUCT, productDao.getProduct(parseId(request)));
        Integer quantity;
        try {
            quantity = getQuantity(request);
        } catch (NumberFormatException | ClassCastException | ParseException | ArithmeticException e) {
            String exceptionMessage = exceptionMap.get(e.getClass().getName());
            request.setAttribute(ATTR_ERROR, exceptionMessage);
            doGet(request, response);
            return;
        }
        Cart cart = (Cart) request.getSession().getAttribute(ATTR_CART);
        try {
            cartService.add(cart, parseId(request), Optional.ofNullable(quantity).orElse(0));
        } catch (OutOfStockException | InvalidQuantityException e) {
            request.setAttribute(ATTR_ERROR, e.getMessage());
            doGet(request, response);
            return;
        }
        request.setAttribute(ATTR_CART, cartService.getCart(request));
        request.setAttribute(ATTR_ERROR, SUCCESS_MSG);
        response.sendRedirect(getServletContext().getContextPath() + PRODUCTS_PATH
                + request.getPathInfo() + "?" + PARAM_ERROR + "=" + SUCCESS_MSG);
    }

    private Integer getQuantity(HttpServletRequest request) throws ParseException, ClassCastException, ArithmeticException {
        String quantityString = request.getParameter(PARAM_QUANTITY);
        Locale locale = request.getLocale();
        numberFormat = NumberFormat.getNumberInstance(locale);
        Integer quantity = Math.toIntExact((Long) numberFormat.parse(quantityString));
        return quantity;
    }

    private Long parseId(HttpServletRequest request) {
        String id = request.getPathInfo().substring(1);
        return Long.parseLong(id);
    }
}
