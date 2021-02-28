<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="cart" type="com.es.phoneshop.cart.Cart" scope="request"/>
<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<jsp:useBean id="recentlyViewed" type="com.es.phoneshop.recentlyviewed.RecentlyViewed" scope="session"/>
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
    <c:if test="${param.error eq 'noError' and empty error}">
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
                    <input inputmode="numeric" class="quantity" value="${1}" name="quantity"/>
                    <button class="add2cart">Add to Cart</button>
                </form>
            </td>

        </tr>
    </table>
    <c:if test="${recentlyViewed.recentlyViewed.size() != 0
    and !(recentlyViewed.recentlyViewed.size() == 1 and recentlyViewed.recentlyViewed[0].id eq product.id)}">
        <p>Recently viewed</p></c:if>
    <c:forEach var="subproduct" items="${recentlyViewed.recentlyViewed}"><c:if
            test="${product.id ne subproduct.id}">
        <div class="bordering">
            <div class="recently">
                <img class="recently" src="${subproduct.imageUrl}">
                <a class="recently"
                   href="${pageContext.servletContext.contextPath}/products/${subproduct.id}">${subproduct.description}</a>
            </div>
        </div>
    </c:if>
    </c:forEach>
</tags:master>
