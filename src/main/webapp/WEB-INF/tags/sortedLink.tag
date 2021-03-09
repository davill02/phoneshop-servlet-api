<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="sort" required="true" %>
<%@ attribute name="order" required="true" %>
<a href="?sort=${sort}&order=${order}&query=${param.query}"
   style="text-decoration: none; ${order eq param.order and sort eq param.sort ? 'color:red' : ''}">
    <c:if test="${order eq 'ASC'}">&uarr;</c:if>
    <c:if test="${order eq 'DESC'}">&darr;</c:if>
</a>

