package com.es.phoneshop.listener;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import static com.es.phoneshop.web.ServletsConstants.PARAM_START_WITH_DEFAULT_PRODUCTS;

public class ProductDaoDemodataServletContextListener implements ServletContextListener {

    private final ProductDao productDao;

    public ProductDaoDemodataServletContextListener() {
        productDao = ArrayListProductDao.getInstance();
    }

    public ProductDaoDemodataServletContextListener(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        boolean startWithDefaultProducts = Boolean.parseBoolean(context.getInitParameter(PARAM_START_WITH_DEFAULT_PRODUCTS));
        if (startWithDefaultProducts) {
            saveDefaultProducts(productDao);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private void saveDefaultProducts(ProductDao productDao) {

        Currency usd = Currency.getInstance("USD");
        Function<PriceHistory, Date> function =
                (Function<PriceHistory, Date> & Serializable) PriceHistory::getDate;
        Comparator<PriceHistory> comparator = Comparator.comparing(function);
        SortedSet<PriceHistory> productHistories = new TreeSet<>(comparator);
        Calendar calendar = new GregorianCalendar(100, Calendar.JANUARY, 0);
        calendar.set(2000, Calendar.AUGUST, 2);
        productHistories.add(new PriceHistory(new BigDecimal(120), usd, calendar.getTime()));
        calendar.set(2004, Calendar.FEBRUARY, 2);
        productHistories.add(new PriceHistory(new BigDecimal(160), usd, calendar.getTime()));
        productDao.save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        productDao.save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", productHistories));
        productHistories = new TreeSet<>(comparator);
        calendar.set(2000, Calendar.DECEMBER, 2);
        productHistories.add(new PriceHistory(new BigDecimal(130), usd, calendar.getTime()));
        calendar.set(2005, Calendar.AUGUST, 3);
        productHistories.add(new PriceHistory(new BigDecimal(140), usd, calendar.getTime()));
        productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", productHistories));
        productDao.save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        productDao.save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        productDao.save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        productDao.save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        productDao.save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        productDao.save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        productDao.save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        productDao.save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        productDao.save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        productDao.save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));

    }
}
