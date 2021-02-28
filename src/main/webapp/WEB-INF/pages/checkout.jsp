<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="order" scope="request" type="com.es.phoneshop.order.Order"/>
<jsp:useBean id="exceptionMap" scope="request" type="java.util.Map"/>
<jsp:useBean id="paymentTypes" scope="request" type="java.util.List"/>
<tags:master pageTitle="Checkout">
    <p style="text-align: center">Checkout</p>
    <c:if test="${order.items.size() ne 0}">
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
                    <td>${item.quantity}</td>
                </tr>
            </c:forEach>
            <tr>
                <td colspan="2">Delivery price</td>
                <td class="price"><fmt:formatNumber value="${order.deliveryPrice}" type="currency"
                                      currencySymbol="${order.items[0].product.currency.symbol}"/></td>
            </tr>
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
        <form name="person" method="post">
                <%--@declare id="person"--%>
            <table style="margin: 0 auto;text-align: center">
                <tr>
                    <td colspan="2"> Details</td>
                </tr>
                <tr>
                    <tags:form exceptionMap="${exceptionMap}" label="First Name (min - 2 symbols)" fieldName="firstname"></tags:form>
                </tr>
                <tr>
                    <tags:form exceptionMap="${exceptionMap}" label="Last Name (min - 2 symbols)" fieldName="lastname"></tags:form>
                </tr>
                <tr>
                    <tags:form exceptionMap="${exceptionMap}" label="Delivery Address (min - 10 symbols)" fieldName="address"></tags:form>
                </tr>
                <tr>
                    <tags:form exceptionMap="${exceptionMap}" label="Delivery Date" type="date" fieldName="date"></tags:form>
                </tr>
                <tr>
                    <td>Payment type<span class="red">*</span></td>
                    <td>
                        <select name="paymentType">
                            <c:forEach var="type" items="${paymentTypes}">
                                <option>${type}</option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <tags:form label="Phone number (min - 7 digits)" exceptionMap="${exceptionMap}" fieldName="phone"></tags:form>
                </tr>
                <tr style="color: white">
                    <td></td>
                    <td><button>Next</button></td>
                </tr>
            </table>

        </form>

    </c:if>
    <c:if test="${cart.items.size() eq 0}">
        <p style="text-align: center">Cart is empty</p>
        <p style="text-align: center">You can choose products</p>
        <form style="text-align: center">
            <input type="button" value="Choose products"
                   onclick="window.location.href = '${pageContext.servletContext.contextPath}/products';">
        </form>
    </c:if>


</tags:master>
