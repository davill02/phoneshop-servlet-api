<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="order" scope="request" type="com.es.phoneshop.order.Order"/>
<tags:master pageTitle="Checkout">
    <p style="text-align: center">Overview</p>
    <table style="margin: 0 auto">
        <thead>
        <tr>
            <td>Image</td>
            <td>Description</td>
            <td class="price">Price</td>
            <td>Quantity</td>
        </tr>
        </thead>
        <c:forEach var="item" items="${order.items}">
            <tr>
                <td>
                    <img class="product-tile" src="${item.product.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                            ${item.product.description}
                    </a>
                </td>
                <td class="price">
                    <fmt:formatNumber value="${item.product.price}" type="currency"
                                      currencySymbol="${item.product.currency.symbol}"/>
                </td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="2">Total Price</td>
            <td class="price">
                <fmt:formatNumber value="${order.totalPrice}" type="currency"
                                  currencySymbol="${order.items[0].product.currency.symbol}"/>
            </td>
            <td>Total quantity: ${order.totalQuantity}</td>
        </tr>
    </table>
    <p></p>
    <table style="margin: 0 auto;text-align: center">
        <tr>
            <td colspan="2"> Details</td>
        </tr>
        <tr>
            <td>Firstname</td>
            <td><input disabled value="${order.person.firstname}"></td>
        </tr>
        <tr>
            <td>Lastname</td>
            <td><input disabled value="${order.person.lastname}"></td>
        </tr>
        <tr>
            <td>Delivery address</td>
            <td><input disabled value="${order.person.address}"></td>
        </tr>
        <tr>
            <td>Phone number</td>
            <td><input disabled value="${order.person.phone}"></td>
        </tr>
        <tr>
            <td>Delivery date</td>
            <td><input disabled value="${order.person.date}"></td>
        </tr>
        <tr>
            <td>Payment type</td>
            <td>${order.type}</td>
        </tr>
    </table>
</tags:master>
