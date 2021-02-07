<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <p>
        Welcome to Expert-Soft training!
    </p>

    <form>
        <input name="query" value="${param.query}">
        <button>Search</button>
    </form>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description
                <tags:sortedLink sort="description" order="asc"></tags:sortedLink>
                <tags:sortedLink sort="description" order="desc">desc</tags:sortedLink>
            </td>
            <td class="price">Price
                <tags:sortedLink sort="price" order="asc">asc</tags:sortedLink>
                <tags:sortedLink sort="price" order="desc">desc</tags:sortedLink>
            </td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}">
            <tr>
                <td>
                    <img class="product-tile" src="${product.imageUrl}">
                </td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                        ${product.description}
                    </a>
                </td>
                <td class="price">
                    <div class="popup" onclick="${product.code}Function()">
                        <fmt:formatNumber value="${product.price}" type="currency"
                                          currencySymbol="${product.currency.symbol}"/>
                        <span class="popuptext" id="${product.code}">
                            <c:forEach var="priceHistory" items="${product.priceHistories}">
                                <p>
                                <fmt:formatNumber value="${priceHistory.price}" type="currency"
                                                  currencySymbol="${priceHistory.currency.symbol}"/>
                                <fmt:formatDate value="${priceHistory.date}"/>
                                </p>
                            </c:forEach>
                        </span>
                        <script>
                            function ${product.code}Function() {
                                document.getElementById("${product.code}").style.visibility = 'visible';
                                setTimeout(function ${product.code}Hide() {
                                    document.getElementById("${product.code}").style.visibility = 'hidden';
                                }, 5000);
                            }
                        </script>
                    </div>
                </td>
            </tr>
        </c:forEach>
    </table>
</tags:master>