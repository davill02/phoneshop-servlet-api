package com.es.phoneshop.order;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

public class PersonalDeliveryData {
    @NotNull(message = "Cant be null")
    @Size(min = 2, max = 50, message = "length is too long or too short")
    private String firstname;
    @NotNull(message = "Cant be null")
    @Size(min = 2, max = 50, message = "length is too long or too short")
    private String lastname;
    @NotNull(message = "Cant be null")
    @Size(min = 10, max = 50, message = "length is too long or too short")
    private String address;
    @NotNull(message = "Cant be null")
    @Future(message = "We cant deliver in past")
    private Date date;
    @NotNull
    @Pattern(regexp = "(\\+?)(\\d(\\s?)){7}(\\d(\\s?))*", message = "Wrong number")
    private String phone;

    public PersonalDeliveryData(String firstname, String lastname, String deliveryAddress, Date deliveryDate, String phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = deliveryAddress;
        this.date = deliveryDate;
        this.phone = phone;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
