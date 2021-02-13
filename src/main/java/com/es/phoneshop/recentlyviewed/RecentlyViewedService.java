package com.es.phoneshop.recentlyviewed;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;

public interface RecentlyViewedService {

    void add(RecentlyViewed recentlyViewed, Product product);

    RecentlyViewed getRecentlyViewed(HttpServletRequest request);
}
