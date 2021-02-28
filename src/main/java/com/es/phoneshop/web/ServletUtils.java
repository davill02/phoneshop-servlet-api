package com.es.phoneshop.web;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class ServletUtils {
    private ServletUtils() {
    }

    public static int parseQuantity(Locale locale, String quantityString) throws ParseException {
        NumberFormat numberFormat;
        numberFormat = NumberFormat.getNumberInstance(locale);
        return Math.toIntExact((Long) numberFormat.parse(quantityString));
    }

}
