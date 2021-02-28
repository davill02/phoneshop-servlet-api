<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>
<%@attribute name="type"  %>
<%@attribute name="label" required="true" %>
<%@attribute name="exceptionMap" required="true" type="java.util.Map" %>
<%@ attribute name="fieldName" required="true" type="java.lang.String" %>
<td>${label}<span class="red">*</span></td>
<c:if test="${exceptionMap[fieldName] eq null}">
    <td><input type="${type}" name="${fieldName}"></td>
</c:if>
<c:if test="${exceptionMap[fieldName] ne null}">
    <td><input type="${type}" name="${fieldName}" value="${exceptionMap[fieldName][0] }">
        <c:if test="${exceptionMap[fieldName][1] ne null}">
        <p class="error">${exceptionMap[fieldName][1]}</p>
        </c:if>
    </td>

</c:if>