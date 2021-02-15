<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="cart" scope="session" type="com.es.phoneshop.cart.Cart"/>
<tags:master pageTitle="Cart">
    <p>Cart</p>
    <c:if test="${cart.items.size() ne 0}">
        <table style="margin: 0 auto">
        <thead>
        <tr>
            <td>Image</td>
            <td>Description</td>
            <td class="price">Price</td>
            <td>Quantity</td>
        </tr>
        </thead>
        <c:forEach var="item" items="${cart.items}">
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
                <td>
                    <input name="quantity" value="${item.quantity}"/>
                </td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="2">Total Price</td>
            <td class="price">
                <fmt:formatNumber value="${cart.totalPrice}" type="currency"
                                  currencySymbol="${cart.currency.symbol}"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${cart.items.size() eq 0}">
        <p>Cart is empty</p>
        <p>You can choose products</p>
        <form>
            <input type="button" value="Choose products"
                   onclick="window.location.href = '${pageContext.servletContext.contextPath}/products';">
        </form>
    </c:if>

    </table>
</tags:master>
