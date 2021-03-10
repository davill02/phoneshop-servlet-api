<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>

<tags:master pageTitle="Product List">

    <p style="text-align: center">
        Welcome to Expert-Soft training!
    </p>
    <c:if test="${not empty exceptionMap['max min']}">
        <p class="error"> ${exceptionMap['max min']}</p>
    </c:if>

    <form style="text-align: center;margin: 10px auto;">
        <input name="query" value="${param.query}">
        <button class="search">Search</button>
        <div>Min price</div>
        <input name="minPrice" value="${param.minPrice}">
        <c:if test="${not empty exceptionMap[param.minPrice]}">
            <p class="error"> ${exceptionMap[param.minPrice]}</p>
        </c:if>
        <div>Max price</div>
        <input name="maxPrice" value="${param.maxPrice}">
        <c:if test="${not empty exceptionMap[param.maxPrice]}">
            <p class="error"> ${exceptionMap[param.maxPrice]}</p>
        </c:if>
        <p>Search type:
            <select name="searchType">
                <c:if test="${empty param.searchType or param.searchType eq 'ALL_WORDS' }">
                    <option>ALL_WORDS</option>
                    <option>ANY_WORD</option>
                </c:if>
                <c:if test="${not empty param.searchType and param.searchType eq 'ANY_WORD' }">
                    <option>ANY_WORD</option>
                    <option>ALL_WORDS</option>
                </c:if>
            </select>
        </p>
    </form>

    <c:if test="${param.error eq 'noError' and empty error}">
        <p class="success">
            Item added to cart
        </p>
    </c:if>
    <c:if test="${not empty error}">
        <p class="error">
                ${error}
        </p>
    </c:if>

    <table style="margin: 0 auto">
        <thead>
        <tr>
            <td>Image</td>
            <td>Description

            </td>
            <td>Quantity</td>
            <td class="price">Price
            </td>
        </tr>
        </thead>
        <c:forEach var="item" items="${products}">

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
            </tr>

        </c:forEach>
    </table>

</tags:master>
