package com.es.phoneshop.order;

import com.es.phoneshop.cart.Cart;

import java.math.BigDecimal;

public class Order extends Cart {
    private PersonalDeliveryData person;
    private BigDecimal subPrice;
    private BigDecimal deliveryPrice;
    private PaymentType type;
    private String secureId;
    private BigDecimal totalPrice;

    public Order() {
    }

    public String getSecureId() {
        return secureId;
    }

    public void setSecureId(String secureId) {
        this.secureId = secureId;
    }

    @Override
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    @Override
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public PersonalDeliveryData getPerson() {
        return person;
    }

    public void setPerson(PersonalDeliveryData person) {
        this.person = person;
    }

    public BigDecimal getSubPrice() {
        return subPrice;
    }

    public void setSubPrice(BigDecimal subPrice) {
        this.subPrice = subPrice;
    }

    public BigDecimal getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(BigDecimal deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }
}
