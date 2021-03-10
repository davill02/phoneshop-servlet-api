package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.search.emuns.SortField;
import com.es.phoneshop.search.emuns.SortOrder;
import com.es.phoneshop.search.engine.ProductSearchEngine;
import com.es.phoneshop.search.engine.SearchEngine;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.es.phoneshop.web.ServletsConstants.LIST_PAGE_PATH;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ERROR;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ERROR_VALUE_NO_ERROR;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ID;
import static com.es.phoneshop.web.ServletsConstants.PARAM_ORDER;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUERY;
import static com.es.phoneshop.web.ServletsConstants.PARAM_SORT;
import static com.es.phoneshop.web.ServletsConstants.PRODUCTS;
import static com.es.phoneshop.web.ServletsConstants.PRODUCTS_PATH;


public class ProductListPageServlet extends AddingToCartServlet {

    private static final String EMPTY_STRING = "";
    private SearchEngine<Product> engine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ProductDao productDao = ArrayListProductDao.getInstance();
        engine = new ProductSearchEngine(productDao.findProducts());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String query = request.getParameter(PARAM_QUERY);
        String order = request.getParameter(PARAM_ORDER);
        String field = request.getParameter(PARAM_SORT);
        request.setAttribute(PRODUCTS, engine.search(query,
                getField(field),
                getOrder(order)));
        request.getRequestDispatcher(LIST_PAGE_PATH).forward(request, response);
    }

    private SortOrder getOrder(String order) {
        try {
            return Optional
                    .ofNullable(order)
                    .map(SortOrder::valueOf)
                    .orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private SortField getField(String field) {
        try {
            return Optional
                    .ofNullable(field)
                    .map(SortField::valueOf)
                    .orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String order = Optional.ofNullable(request.getParameter(PARAM_ORDER)).orElse(EMPTY_STRING);
        String sort = Optional.ofNullable(request.getParameter(PARAM_SORT)).orElse(EMPTY_STRING);
        String query = Optional.ofNullable(request.getParameter(PARAM_QUERY)).orElse(EMPTY_STRING);
        response.sendRedirect(getServletContext().getContextPath() + PRODUCTS_PATH
                + "?" + PARAM_ERROR + "=" + PARAM_ERROR_VALUE_NO_ERROR
                + "&" + PARAM_ORDER + "=" + order
                + "&" + PARAM_SORT + "=" + sort
                + "&" + PARAM_QUERY + "=" + query
        );
    }

    @Override
    protected Long parseId(HttpServletRequest request) {
        String id = request.getParameter(PARAM_ID);
        return Long.parseLong(id);
    }
}
