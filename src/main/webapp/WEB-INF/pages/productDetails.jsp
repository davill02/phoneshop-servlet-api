<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="cart" type="com.es.phoneshop.cart.Cart" scope="request"/>
<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Product">
    <p class="description">
            ${product.description}
    </p>
    <p>
        ${cart.items}
    </p>
    <c:if test="${not empty error}">
        <p class="error">
            ${error}
        </p>
    </c:if>
    <c:if test="${param.error eq 'noError'}">
        <p class="success">
            ${product.description} added to cart
        </p>
    </c:if>
    <table class="details">
        <tr>
            <td>Image</td>
            <td class="image"><img src="${product.imageUrl}"></td>
        </tr>
        <tr>
            <td>code</td>
            <td>${product.code}</td>
        </tr>
        <tr>
            <td>stock</td>
            <td>${product.stock}</td>
        </tr>
        <tr>
            <td>price</td>
            <td><fmt:formatNumber value="${product.price}" type="currency"
                                  currencySymbol="${product.currency.symbol}"/></td>
        </tr>
        <tr>
            <td>quantity</td>
            <td class="quantity">
                <form class="quantity" method="post">
                    <input inputmode="numeric" class="quantity" name="quantity"/>
                    <button class="add2cart">Add to Cart</button>
                </form>
            </td>

        </tr>
    </table>
</tags:master>
