package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.search.emuns.SearchType;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.es.phoneshop.web.ServletsConstants.ADVANCED_SEARCH_PAGE_PATH;
import static com.es.phoneshop.web.ServletsConstants.ATTR_EXCEPTION_MAP;
import static com.es.phoneshop.web.ServletsConstants.PARAM_MAX_PRICE;
import static com.es.phoneshop.web.ServletsConstants.PARAM_MIN_PRICE;
import static com.es.phoneshop.web.ServletsConstants.PARAM_QUERY;
import static com.es.phoneshop.web.ServletsConstants.PARAM_SEARCH_TYPE;
import static com.es.phoneshop.web.ServletsConstants.PRODUCTS;
import static com.es.phoneshop.web.ServletsExceptionMessages.CHANGE_MAX_AND_MIN_MESSAGE;


public class AdvanceSearchPageServlet extends HttpServlet {

    public static final String EMPTY_STRING = "";

    public static final String MAX_MIN = "max min";
    private ProductDao productDao;
    private Map<String, String> exceptions = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDao = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        exceptions.clear();
        String minPriceStr = Optional.ofNullable(request.getParameter(PARAM_MIN_PRICE)).orElse(EMPTY_STRING).trim();
        String maxPriceStr = Optional.ofNullable(request.getParameter(PARAM_MAX_PRICE)).orElse(EMPTY_STRING).trim();
        String query = Optional.ofNullable(request.getParameter(PARAM_QUERY)).orElse(EMPTY_STRING).trim();
        String searchType = Optional.ofNullable(request.getParameter(PARAM_SEARCH_TYPE)).orElse(EMPTY_STRING).trim();
        if (EMPTY_STRING.equals(minPriceStr) && EMPTY_STRING.equals(maxPriceStr) && EMPTY_STRING.equals(query)) {
            request.setAttribute(PRODUCTS, getAllProducts());
        } else {
            SearchType search = SearchType.valueOf(searchType);
            int maxPrice = Integer.MAX_VALUE;
            if (!EMPTY_STRING.equals(maxPriceStr.trim())) {
                maxPrice = getPrice(maxPriceStr);
            }
            int minPrice = 0;
            if (!EMPTY_STRING.equals(minPriceStr.trim())) {
                minPrice = getPrice(minPriceStr);
            }
            if (minPrice > maxPrice) {
                exceptions.put(MAX_MIN, CHANGE_MAX_AND_MIN_MESSAGE);
            }

            if (exceptions.isEmpty()) {
                setProductsBySearchType(request, minPrice, maxPrice, query, search);
            } else {
                request.setAttribute(PRODUCTS, new ArrayList<Product>());
            }
        }
        request.setAttribute(ATTR_EXCEPTION_MAP, exceptions);
        request.getRequestDispatcher(ADVANCED_SEARCH_PAGE_PATH).forward(request, response);
    }

    @NotNull
    private List<Product> getAllProducts() {
        return productDao.getProducts()
                .stream()
                .collect(Collectors.toList());
    }

    private int getPrice(String priceStr) {
        int price = 0;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            exceptions.put(priceStr, ServletsExceptionMessages.CANT_PARSE_VALUE);
        }
        return price;
    }

    private void setProductsBySearchType(HttpServletRequest request, int minPrice, int maxPrice,
                                         String query, SearchType search) {
        if (search.equals(SearchType.ALL_WORDS)) {
            request.setAttribute(PRODUCTS, getAllWordsList(query, minPrice, maxPrice));
        } else {
            request.setAttribute(PRODUCTS, getAnyWordList(query, minPrice, maxPrice));
        }
    }

    private List<Product> getAllWordsList(String query, int minPrice, int maxPrice) {
        return getProductStream(minPrice, maxPrice)
                .filter(product -> containsAllWords(product.getDescription(), query))
                .collect(Collectors.toList());
    }

    @NotNull
    private Stream<Product> getProductStream(int minPrice, int maxPrice) {
        return productDao.findProducts().stream()
                .filter(product -> product.getPrice().compareTo(new BigDecimal(minPrice)) >= 0)
                .filter(product -> product.getPrice().compareTo(new BigDecimal(maxPrice)) <= 0);
    }


    private boolean containsAllWords(String description, String query) {
        StringTokenizer tokens = new StringTokenizer(query);
        while (tokens.hasMoreTokens()) {
            if (!description.contains(tokens.nextToken())) {
                return false;
            }
        }
        return true;
    }

    private List<Product> getAnyWordList(String query, int minPrice, int maxPrice) {
        return getProductStream(minPrice, maxPrice)
                .filter(product -> containsAnyWord(product.getDescription(), query))
                .collect(Collectors.toList());
    }

    private boolean containsAnyWord(String description, String query) {
        StringTokenizer tokens = new StringTokenizer(query);
        while (tokens.hasMoreTokens()) {
            if (description.contains(tokens.nextToken())) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> getExceptions() {
        return exceptions;
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }
}
