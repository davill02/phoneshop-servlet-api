package com.es.phoneshop.recentlyviewed;

import com.es.phoneshop.model.product.Product;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;

import static com.es.phoneshop.web.ServletsConstants.ATTR_RECENTLY_VIEWED;


public class DefaultRecentlyViewedService implements RecentlyViewedService {
    public static final int MAX_COUNT_RECENTLY_VIEWED = 3;

    private static RecentlyViewedService service;

    private DefaultRecentlyViewedService() {
    }

    public synchronized static RecentlyViewedService getInstance() {
        if (service == null) {
            service = new DefaultRecentlyViewedService();
        }
        return service;
    }

    @Override
    public synchronized void add(RecentlyViewed recentlyViewed, Product product) {
        recentlyViewed.getRecentlyViewed().remove(product);
        recentlyViewed.getRecentlyViewed().add(0, product);
        deleteIfMoreThanConstant(recentlyViewed);
    }

    private void deleteIfMoreThanConstant(RecentlyViewed recentlyViewed) {
        if (recentlyViewed.getRecentlyViewed().size() >= MAX_COUNT_RECENTLY_VIEWED + 1) {
            while (recentlyViewed.getRecentlyViewed().size() != MAX_COUNT_RECENTLY_VIEWED) {
                recentlyViewed.getRecentlyViewed().remove(recentlyViewed.getRecentlyViewed().size() - 1);
            }
        }
    }

    @Override
    public synchronized RecentlyViewed getRecentlyViewed(HttpServletRequest request) {
        RecentlyViewed recentlyViewed = null;
        if (request != null) {
            recentlyViewed = (RecentlyViewed) request.getSession().getAttribute(ATTR_RECENTLY_VIEWED);
            recentlyViewed = getRecentlyViewed(request, recentlyViewed);
        }
        return recentlyViewed;
    }

    @NotNull
    private RecentlyViewed getRecentlyViewed(HttpServletRequest request, RecentlyViewed recentlyViewed) {
        if (recentlyViewed == null) {
            recentlyViewed = new RecentlyViewed();
            request.getSession().setAttribute(ATTR_RECENTLY_VIEWED, recentlyViewed);
        }
        return recentlyViewed;
    }
}
