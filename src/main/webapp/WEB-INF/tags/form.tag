<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@attribute name="label" required="true" %>
<%@attribute name="exceptionMap" required="true" type="java.util.Map" %>
<%@ attribute name="fieldName" required="true" type="java.lang.String" %>
<td>${label}</td>
<c:if test="${exceptionMap[fieldName] eq null}">
    <td><input name="${fieldName}"></td>
</c:if>
<c:if test="${exceptionMap[fieldName] ne null}">
    <td><input name="${fieldName}" value="${exceptionMap[fieldName][0] }">
        <c:if test="${exceptionMap[fieldName][1] ne null}">
        <p class="error">${exceptionMap[fieldName][1]}</p>
        </c:if>
    </td>

</c:if>