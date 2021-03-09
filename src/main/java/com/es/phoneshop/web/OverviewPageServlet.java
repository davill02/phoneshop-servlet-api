package com.es.phoneshop.web;

import com.es.phoneshop.order.ArrayListOrderDao;
import com.es.phoneshop.order.OrderDao;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.es.phoneshop.web.ServletsConstants.ATTR_ORDER;
import static com.es.phoneshop.web.ServletsConstants.OVERVIEW_PAGE_PATH;

public class OverviewPageServlet extends HttpServlet {
    private OrderDao orderDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String secureId = request.getPathInfo().substring(1);
        request.setAttribute(ATTR_ORDER, orderDao.getOrder(secureId));
        request.getRequestDispatcher(OVERVIEW_PAGE_PATH).forward(request, response);
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }
}
