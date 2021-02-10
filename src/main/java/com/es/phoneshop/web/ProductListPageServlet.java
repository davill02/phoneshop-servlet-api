package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.search.emuns.SortField;
import com.es.phoneshop.search.emuns.SortOrder;
import com.es.phoneshop.search.engine.ProductSearchEngine;
import com.es.phoneshop.search.engine.SearchEngine;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    private static final String PRODUCTS = "products";
    private static final String PAGE_PATH = "/WEB-INF/pages/productList.jsp";
    private static final String PARAM_QUERY = "query";
    private static final String PARAM_ORDER = "order";
    private static final String PARAM_SORT = "sort";

    private ProductDao productDao;
    private SearchEngine engine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
        engine = new ProductSearchEngine(productDao.findProducts());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter(PARAM_QUERY);
        String order = request.getParameter(PARAM_ORDER);
        String field = request.getParameter(PARAM_SORT);
        request.setAttribute(PRODUCTS, engine.search(query,
                Optional.ofNullable(field).map(SortField::valueOf).orElse(null),
                Optional.ofNullable(order).map(SortOrder::valueOf).orElse(null)));
        request.getRequestDispatcher(PAGE_PATH).forward(request, response);
    }


}
