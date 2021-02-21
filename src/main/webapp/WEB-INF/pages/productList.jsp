<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>

<tags:master pageTitle="Product List">

    <p style="text-align: center">
        Welcome to Expert-Soft training!
    </p>
    <form style="text-align: center;margin: 10px auto;">
        <input type="hidden" name="sort" value="${param.sort}">
        <input type="hidden" name="order" value="${param.order}">
        <input name="query" value="${param.query}">
        <button class="search">Search</button>
    </form>

    <c:if test="${param.error eq 'noError' and empty exceptionMap}">
        <p class="success">
                ${param.name} added to cart
        </p>
    </c:if>
    <table style="margin: 0 auto">
        <thead>
        <tr>
            <td>Image</td>
            <td>Description
                <tags:sortedLink sort="description" order="asc"></tags:sortedLink>
                <tags:sortedLink sort="description" order="desc">desc</tags:sortedLink>
            </td>
            <td>Quantity</td>
            <td class="price">Price
                <tags:sortedLink sort="price" order="asc">asc</tags:sortedLink>
                <tags:sortedLink sort="price" order="desc">desc</tags:sortedLink>
            </td>
        </tr>
        </thead>
        <c:forEach var="item" items="${products}">
        <form method="post">
            <tr>
                <td>
                    <img class="product-tile" src="${item.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${item.id}">
                            ${item.description}
                    </a>
                </td>
                <td>
                    <input name="quantity" value="${1}">
                    <input type="hidden" name="id" value="${item.id}">
                </td>
                <td class="price">
                    <div class="popup" onclick="${item.code}Function()">
                        <fmt:formatNumber value="${item.price}" type="currency"
                                          currencySymbol="${item.currency.symbol}"/>
                        <span class="popuptext" id="${item.code}">
                            <c:forEach var="priceHistory" items="${item.priceHistories}">
                                <p>
                                <fmt:formatNumber value="${priceHistory.price}" type="currency"
                                                  currencySymbol="${priceHistory.currency.symbol}"/>
                                <fmt:formatDate value="${priceHistory.date}"/>
                                </p>
                            </c:forEach>
                        </span>
                        <script>
                            function ${item.code}Function() {
                                document.getElementById("${item.code}").style.visibility = 'visible';
                                setTimeout(function ${item.code}Hide() {
                                    document.getElementById("${item.code}").style.visibility = 'hidden';
                                }, 5000);
                            }
                        </script>
                    </div>
                </td>
                <td><button>Add to cart</button></td>
            </tr>
            </form>
        </c:forEach>
    </table>

</tags:master>
