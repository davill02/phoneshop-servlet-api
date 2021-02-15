package com.es.phoneshop.recentlyviewed;

import com.es.phoneshop.model.product.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecentlyViewed implements Serializable {
    private List<Product> recentlyViewed;

    public RecentlyViewed() {
        recentlyViewed = new ArrayList<>();
    }

    public List<Product> getRecentlyViewed() {
        return recentlyViewed;
    }

    public void setRecentlyViewed(List<Product> recentlyViewed) {
        this.recentlyViewed = recentlyViewed;
    }
}
