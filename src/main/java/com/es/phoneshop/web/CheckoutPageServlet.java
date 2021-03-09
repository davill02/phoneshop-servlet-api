package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.DefaultCartService;
import com.es.phoneshop.order.DefaultOrderService;
import com.es.phoneshop.order.Order;
import com.es.phoneshop.order.OrderService;
import com.es.phoneshop.order.PaymentType;
import com.es.phoneshop.order.PersonalDeliveryData;
import com.es.phoneshop.order.exceptions.InvalidOrderException;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.es.phoneshop.web.ServletsConstants.*;

public class CheckoutPageServlet extends HttpServlet {
    public static final String EMPTY_STRING = "";
    public static final String CANT_PARSE_DATE = "Cant parse date";
    public static final String HTML_INPUT_DATE_PATTERN = "yyyy-MM-dd";


    private CartService cartService;
    private OrderService orderService;
    private SimpleDateFormat simpleDateFormat;
    private Map<String, List<String>> exceptionMap;
    private boolean isSentExceptions = false;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
        simpleDateFormat = new SimpleDateFormat(HTML_INPUT_DATE_PATTERN);
        exceptionMap = new HashMap<>();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isSentExceptions) {
            exceptionMap.clear();
        } else {
            isSentExceptions = false;
        }
        request.setAttribute(ATTR_EXCEPTION_MAP, exceptionMap);
        Cart cart = cartService.getCart(request);
        request.setAttribute(ATTR_ORDER, orderService.getOrder(cart));
        request.setAttribute(ATTR_PAYMENT_TYPES, orderService.getPaymentTypes());
        request.getRequestDispatcher(CHECKOUT_PAGE_PATH).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        exceptionMap.clear();
        String firstname = request.getParameter(PARAM_FIRSTNAME);
        String lastname = request.getParameter(PARAM_LASTNAME);
        String address = request.getParameter(PARAM_ADDRESS);
        String dateString = request.getParameter(PARAM_DATE);
        String phone = request.getParameter(PARAM_PHONE);
        String paymentType = request.getParameter(PARAM_PAYMENT_TYPE);
        fillExceptionMap(firstname, lastname, address, dateString, phone);
        try {
            if (cartService.getCart(request) != null
                    && orderService.isAvailableInStock(cartService.getCart(request))) {
                Date date = getDate(dateString);
                PersonalDeliveryData person = new PersonalDeliveryData(firstname, lastname, address, date, phone);
                validateAndChoosePage(request, response, paymentType, person);
                return;
            }
        } catch (InvalidOrderException ignored) {
        }
        response.sendRedirect(request.getContextPath() + PRODUCTS_PATH + CART_PATH
                + "?" + PARAM_ERROR + "=" + PARAM_ERROR_VALUE_OUT_OF_STOCK);
    }

    private void validateAndChoosePage(HttpServletRequest request, HttpServletResponse response, String paymentType,
                                       PersonalDeliveryData person)
            throws IOException, ServletException, InvalidOrderException {
        Set<ConstraintViolation<PersonalDeliveryData>> violations = getConstraintViolations(person);
        if (violations.isEmpty()) {
            Order orderWithSecureId = handleOrder(request, paymentType, person);
            response.sendRedirect(getServletContext().getContextPath()
                    + ORDER_PATH + OVERVIEW_PATH + "/" + orderWithSecureId.getSecureId());
        } else {
            fillExceptionMap(violations);
            isSentExceptions = true;
            request.setAttribute(ATTR_PAYMENT_TYPES, orderService.getPaymentTypes());
            doGet(request, response);
        }
    }


    private void fillExceptionMap(Set<ConstraintViolation<PersonalDeliveryData>> violations) {
        for (ConstraintViolation<PersonalDeliveryData> i : violations) {
            exceptionMap.put(i.getPropertyPath().toString(), getValidInfoList(i));
        }
    }

    private Order handleOrder(HttpServletRequest request, String paymentType, PersonalDeliveryData person)
            throws InvalidOrderException {
        Order order = orderService.getOrder(cartService.getCart(request));
        order.setPerson(person);
        order.setType(getPaymentType(paymentType));
        String secureId = UUID.randomUUID().toString();
        while (orderService.containsOrder(secureId)) {
            secureId = UUID.randomUUID().toString();
        }
        order.setSecureId(secureId);
        orderService.placeOrder(order);
        request.getSession().removeAttribute(ATTR_CART);
        return order;
    }


    @Nullable
    private Date getDate(String dateString) {
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            putDateException(dateString);
        }
        return date;
    }

    private void putDateException(String dateString) {
        List<String> info = new ArrayList<>();
        info.add(dateString);
        info.add(CANT_PARSE_DATE);
        exceptionMap.put(PARAM_DATE, info);
    }

    private Set<ConstraintViolation<PersonalDeliveryData>> getConstraintViolations(PersonalDeliveryData person) {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        return validator.validate(person);
    }

    private void fillExceptionMap(String firstname, String lastname, String address, String dateString, String phone) {
        exceptionMap.put(PARAM_FIRSTNAME, createParamList(firstname));
        exceptionMap.put(PARAM_LASTNAME, createParamList(lastname));
        exceptionMap.put(PARAM_ADDRESS, createParamList(address));
        exceptionMap.put(PARAM_DATE, createParamList(dateString));
        exceptionMap.put(PARAM_PHONE, createParamList(phone));
    }

    private List<String> createParamList(String param) {
        List<String> result = new ArrayList<>();
        result.add(param);
        return result;
    }

    private List<String> getValidInfoList(ConstraintViolation<PersonalDeliveryData> violation) {
        List<String> info = new ArrayList<>();
        if (violation.getInvalidValue() != null) {
            info.add(violation.getInvalidValue().toString());
        } else {
            info.add(EMPTY_STRING);
        }
        info.add(violation.getMessage());
        return info;
    }

    private PaymentType getPaymentType(String paymentType) {
        PaymentType result;
        try {
            result = PaymentType.valueOf(paymentType);
        } catch (IllegalArgumentException e) {
            result = PaymentType.CASH;
        }
        return result;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setExceptionMap(Map<String, List<String>> exceptionMap) {
        this.exceptionMap = exceptionMap;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}
