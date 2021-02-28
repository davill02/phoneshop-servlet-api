package com.es.phoneshop.web;

import com.es.phoneshop.cart.Cart;
import com.es.phoneshop.cart.CartService;
import com.es.phoneshop.cart.DefaultCartService;
import com.es.phoneshop.order.DefaultOrderService;
import com.es.phoneshop.order.Order;
import com.es.phoneshop.order.OrderService;
import com.es.phoneshop.order.PaymentType;
import com.es.phoneshop.order.PersonalDeliveryData;
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
import java.text.DateFormat;
import java.text.ParseException;
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
    private CartService cartService;
    private OrderService orderService;
    private DateFormat dateFormat;
    private Map<String, List<String>> exceptionMap;
    private boolean isSentExceptions = false;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
        exceptionMap = new HashMap<>();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        exceptionMap.clear();
        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, request.getLocale());
        String firstname = request.getParameter(PARAM_FIRSTNAME);
        String lastname = request.getParameter(PARAM_LASTNAME);
        String address = request.getParameter(PARAM_ADDRESS);
        String dateString = request.getParameter(PARAM_DATE);
        String phone = request.getParameter(PARAM_PHONE);
        String paymentType = request.getParameter(PARAM_PAYMENT_TYPE);
        fillExceptionMap(firstname, lastname, address, dateString, phone);
        Date date = getDate(dateString);
        PersonalDeliveryData person = new PersonalDeliveryData(firstname, lastname, address, date, phone);
        Set<ConstraintViolation<PersonalDeliveryData>> violations = getConstraintViolations(person);
        if (violations.isEmpty()) {
            Order orderWithSecureId = handleOrder(request, paymentType, person);
            response.sendRedirect(getServletContext().getContextPath() + ORDER_PATH + OVERVIEW_PATH + "/" + orderWithSecureId.getSecureId());
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

    private Order handleOrder(HttpServletRequest request, String paymentType, PersonalDeliveryData person) {
        Order order = orderService.getOrder(cartService.getCart(request));
        order.setPerson(person);
        order.setType(getPaymentType(paymentType));
        String secureId = UUID.randomUUID().toString();
        order.setSecureId(secureId);
        orderService.placeOrder(order);
        return order;
    }

    @Nullable
    private Date getDate(String dateString) {
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            putDateException();
        }
        return date;
    }

    private void putDateException() {
        List<String> info = new ArrayList<>();
        info.add(PARAM_DATE);
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
            result = PaymentType.cash;
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
